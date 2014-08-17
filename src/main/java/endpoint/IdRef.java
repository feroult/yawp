package endpoint;

public class IdRef<T> implements Comparable<IdRef<T>> {

	private Class<T> clazz;

	private Long id;

	private Repository r;

	protected IdRef(Repository r, Class<T> clazz, long id) {
		this.clazz = clazz;
		this.id = id;
		this.r = r;
	}

	public T fetch() {
		return r.query(clazz).id(id);
	}

	public <TT> TT fetch(Class<TT> childClazz) {
		return r.query(childClazz).id(id);
	}

	public Long asLong() {
		return id;
	}

	public static <TT> IdRef<TT> create(Repository r, Class<TT> clazz, Long id) {
		return new IdRef<TT>(r, clazz, id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdRef other = (IdRef) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(IdRef<T> o) {
		return id.compareTo(o.asLong());
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
