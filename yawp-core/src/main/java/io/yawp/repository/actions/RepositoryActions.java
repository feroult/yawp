package io.yawp.repository.actions;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.util.Map;

public class RepositoryActions {

    private RepositoryActions() {}

    public static Object execute(Repository r, ActionMethod actionMethod, IdRef<?> id, String json, Map<String, String> params) {
        boolean rollback = false;
        atomicBegin(r, actionMethod);

        try {

            return actionMethod.invoke(r, id, json, params);

        } catch (Throwable t) {
            rollback = true;
            atomicRollback(r);
            throw t;

        } finally {
            atomicCommit(r, rollback);
        }
    }

    private static void atomicCommit(Repository r, boolean rollback) {
        if (r.isTransationInProgress()) {
            if (rollback) {
                throw new RuntimeException(
                        "Running on devserver or unit tests? To test cross-group, default_high_rep_job_policy_unapplied_job_pct must be > 0");
            }

            r.commit();
        }
    }

    private static void atomicRollback(Repository r) {
        if (r.isTransationInProgress()) {
            r.rollback();
        }
    }

    private static void atomicBegin(Repository r, ActionMethod actionMethod) {
        if (actionMethod.isAtomic()) {
            if (actionMethod.isAtomicCrossEntities()) {
                r.beginX();
            } else {
                r.begin();
            }
        }
    }
}
