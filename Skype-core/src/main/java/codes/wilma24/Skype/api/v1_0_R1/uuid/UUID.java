package codes.wilma24.Skype.api.v1_0_R1.uuid;

public class UUID {

	private String uuid;

	private UUID(String input) {
		this.uuid = input.replace("-", "");
	}

	public static UUID nameUUIDFromString(String name) {
		return new UUID(java.util.UUID.nameUUIDFromBytes(name.getBytes())
				.toString());
	}

	public static UUID nameUUIDFromBytes(byte[] name) {
		return new UUID(java.util.UUID.nameUUIDFromBytes(name).toString());
	}

	public static UUID fromString(String input) throws IllegalArgumentException {
		java.util.UUID uuid = java.util.UUID
				.fromString(input
						.replace("-", "")
						.replaceFirst(
								"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
								"$1-$2-$3-$4-$5"));
		return new UUID(uuid.toString());
	}

	public static UUID randomUUID() {
		return new UUID(java.util.UUID.randomUUID().toString());
	}

	@Deprecated
	public java.util.UUID getUUID() {
		java.util.UUID uuid = java.util.UUID
				.fromString(this.uuid
						.replaceFirst(
								"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
								"$1-$2-$3-$4-$5"));
		return uuid;
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return uuid.equals(obj.toString());
	}

	public int compareTo(UUID val) {
		return uuid.compareTo(val.toString());
	}

	@Override
	public String toString() {
		return uuid;
	}

}
