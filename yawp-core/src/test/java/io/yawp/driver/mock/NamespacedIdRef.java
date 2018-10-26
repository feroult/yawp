package io.yawp.driver.mock;

import io.yawp.repository.IdRef;

public class NamespacedIdRef {
    private String namespace;
    private IdRef<?> id;

    public NamespacedIdRef(String namespace, IdRef<?> id) {
        this.namespace = namespace == null ? "" : namespace;
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NamespacedIdRef other = (NamespacedIdRef) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        return true;
    }

    public boolean isFrom(String currentNamespace) {
        currentNamespace = currentNamespace == null ? "" : currentNamespace;
        return this.namespace.equals(currentNamespace);
    }

}
