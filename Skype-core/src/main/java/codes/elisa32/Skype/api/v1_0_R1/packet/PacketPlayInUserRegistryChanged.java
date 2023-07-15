package codes.elisa32.Skype.api.v1_0_R1.packet;


public class PacketPlayInUserRegistryChanged extends Packet {

	private Object payload;

	public PacketPlayInUserRegistryChanged(Object payload) {
		super(PacketType.USER_REGISTRY_CHANGED_IN);
		this.setPayload(payload.toString());
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

}
