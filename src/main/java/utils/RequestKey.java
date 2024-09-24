package utils;

public class RequestKey {
	private final String clientIP;
	private final int clientPort;
	private final int requestId;

	public RequestKey(String clientIP, int clientPort, int requestId) {
		this.clientIP = clientIP;
		this.clientPort = clientPort;
		this.requestId = requestId;
	}

	@Override
	public int hashCode() {
		return clientIP.hashCode() + clientPort + requestId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		RequestKey that = (RequestKey) obj;
		return clientPort == that.clientPort && requestId == that.requestId && clientIP.equals(that.clientIP);
	}
}
