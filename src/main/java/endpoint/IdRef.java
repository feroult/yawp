package endpoint;

public class IdRef<T> {

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

	public Long asLong() {
		return id;
	}

	public static <TT> IdRef<TT> create(Repository r, Class<TT> clazz, Long id) {
		return new IdRef<TT>(r, clazz, id);
	}
}
