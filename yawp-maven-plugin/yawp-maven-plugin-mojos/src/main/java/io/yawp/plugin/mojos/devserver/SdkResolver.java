/**
 * Copyright 2012 Google Inc. All Rights Reserved.
 */
package io.yawp.plugin.mojos.devserver;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.version.Version;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.google.common.collect.Iterables.find;

/**
 * Resolves the sdk in the .m2 repository, retrieving the appropriate version
 * for this version of the plugin if necessary.
 *
 * @author Matt Stephenson <mattstep@google.com>
 */
public class SdkResolver {
    private static final String SDK_GROUP_ID = "com.google.appengine";
    private static final String SDK_ARTIFACT_ID = "appengine-java-sdk";
    private static final String SDK_EXTENSION = "zip";

    @SuppressWarnings("unchecked")
    public static File getSdk(MavenProject project, RepositorySystem repoSystem, RepositorySystemSession repoSession,
                              List<RemoteRepository>... repos) throws MojoExecutionException {
        Artifact artifact = (Artifact) find(project.getPluginArtifacts(), new Predicate<Artifact>() {
            @Override
            public boolean apply(Artifact artifact1) {
                return artifact1.getArtifactId().equals("appengine-maven-plugin");
            }
        });

        String version = artifact.getVersion();

        if (version.endsWith("-SNAPSHOT")) {
            String newestVersion = determineNewestVersion(repoSystem, repoSession, repos);
            return getSdk(newestVersion, repoSystem, repoSession, repos);
        }

        return getSdk(version, repoSystem, repoSession, repos);
    }

    private static String determineNewestVersion(RepositorySystem repoSystem, RepositorySystemSession repoSession,
                                                 List<RemoteRepository>[] repos) throws MojoExecutionException {
        String version;
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(new DefaultArtifact(SDK_GROUP_ID + ":" + SDK_ARTIFACT_ID + ":[0,)"));
        for (List<RemoteRepository> repoList : repos) {
            for (RemoteRepository repo : repoList) {
                rangeRequest.addRepository(repo);
            }
        }

        VersionRangeResult rangeResult;
        try {
            rangeResult = repoSystem.resolveVersionRange(repoSession, rangeRequest);
        } catch (VersionRangeResolutionException e) {
            throw new MojoExecutionException("Could not resolve latest version of the App Engine Java SDK", e);
        }

        List<Version> versions = rangeResult.getVersions();

        Collections.sort(versions);

        Version newest = Iterables.getLast(versions);

        version = newest.toString();
        return version;
    }

    @SuppressWarnings({"unchecked", "resource"})
    public static File getSdk(String version, RepositorySystem repoSystem, RepositorySystemSession repoSession,
                              List<RemoteRepository>... repos) throws MojoExecutionException {

        List<RemoteRepository> allRepos = ImmutableList.copyOf(Iterables.concat(repos));

        ArtifactRequest request = new ArtifactRequest(new DefaultArtifact(SDK_GROUP_ID, SDK_ARTIFACT_ID, SDK_EXTENSION, version), allRepos,
                null);

        ArtifactResult result;
        try {
            result = repoSystem.resolveArtifact(repoSession, request);
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Could not resolve SDK artifact in Maven.", e);
        }

        File sdkArchive = result.getArtifact().getFile();
        File sdkRepoDir = sdkArchive.getParentFile();
        File sdkBaseDir = new File(sdkRepoDir, SDK_ARTIFACT_ID);

        if (sdkBaseDir.exists() && !sdkBaseDir.isDirectory()) {
            throw new MojoExecutionException("Could not unpack the SDK because there is an unexpected file at " + sdkBaseDir
                    + " which conflicts with where we plan to unpack the SDK.");
        }

        if (!sdkBaseDir.exists()) {
            sdkBaseDir.mkdirs();
        }

        // While processing the zip archive, if we find an initial entry that is
        // a directory, and all entries are a child
        // of this directory, then we append this to the sdkBaseDir we return.
        String sdkBaseDirSuffix = null;

        try {
            ZipFile sdkZipArchive = new ZipFile(sdkArchive);
            Enumeration<? extends ZipEntry> zipEntries = sdkZipArchive.entries();

            if (!zipEntries.hasMoreElements()) {
                throw new MojoExecutionException("The SDK zip archive appears corrupted.  There are no entries in the zip index.");
            }

            ZipEntry firstEntry = zipEntries.nextElement();
            if (firstEntry.isDirectory()) {
                sdkBaseDirSuffix = firstEntry.getName();
            } else {
                // Reinitialize entries
                zipEntries = sdkZipArchive.entries();
            }

            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();

                if (!zipEntry.isDirectory()) {
                    File zipEntryDestination = new File(sdkBaseDir, zipEntry.getName());

                    if (!zipEntry.getName().startsWith(sdkBaseDirSuffix)) {
                        // We found an entry that doesn't use this initial base
                        // directory, oh well, just set it to null.
                        sdkBaseDirSuffix = null;
                    }

                    if (!zipEntryDestination.exists()) {
                        Files.createParentDirs(zipEntryDestination);
                        Files.write(ByteStreams.toByteArray(sdkZipArchive.getInputStream(zipEntry)), zipEntryDestination);
                    }
                }
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Could not open SDK zip archive.", e);
        }

        if (sdkBaseDirSuffix == null) {
            return sdkBaseDir;
        }

        return new File(sdkBaseDir, sdkBaseDirSuffix);
    }
}