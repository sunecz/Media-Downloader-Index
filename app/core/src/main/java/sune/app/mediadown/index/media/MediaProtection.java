package sune.app.mediadown.index.media;

import java.util.Objects;

public final class MediaProtection {
	
	private final MediaProtectionType type;
	private final String scheme;
	private final String contentType;
	private final String content;
	private final String keyId;
	
	private MediaProtection(MediaProtectionType type, String scheme, String contentType, String content, String keyId) {
		this.type = Objects.requireNonNull(type);
		this.scheme = Objects.requireNonNull(scheme);
		this.contentType = Objects.requireNonNull(contentType);
		this.content = Objects.requireNonNull(content);
		this.keyId = keyId; // May be null
	}
	
	public static Builder of(MediaProtectionType type) {
		return new Builder(type);
	}
	
	public static Builder ofWidevine() {
		return of(MediaProtectionType.DRM_WIDEVINE);
	}
	
	public static Builder ofPlayReady() {
		return of(MediaProtectionType.DRM_PLAYREADY);
	}
	
	public static Builder ofFairPlay() {
		return of(MediaProtectionType.DRM_FAIRPLAY);
	}
	
	public static Builder ofUnknown() {
		return of(MediaProtectionType.UNKNOWN);
	}
	
	public MediaProtectionType type() {
		return type;
	}
	
	public String scheme() {
		return scheme;
	}
	
	public String contentType() {
		return contentType;
	}
	
	public String content() {
		return content;
	}
	
	public String keyId() {
		return keyId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, scheme, contentType, content, keyId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		MediaProtection other = (MediaProtection) obj;
		return type == other.type
				&& Objects.equals(scheme, other.scheme)
				&& Objects.equals(contentType, other.contentType)
				&& Objects.equals(content, other.content)
				&& Objects.equals(keyId, other.keyId);
	}
	
	@Override
	public String toString() {
		return "MediaProtection["
					+ "type=" + type + ", "
					+ "scheme=" + scheme + ", "
					+ "contentType=" + contentType + ", "
					+ "content=" + content + ", "
					+ "keyId=" + keyId
		        + "]";
	}
	
	public static final class Builder {
		
		private final MediaProtectionType type;
		private String scheme;
		private String contentType;
		private String content;
		private String keyId;
		
		public Builder(MediaProtectionType type) {
			this.type = Objects.requireNonNull(type);
		}
		
		public MediaProtection build() {
			return new MediaProtection(type, scheme, contentType, content, keyId);
		}
		
		public Builder scheme(String scheme) {
			this.scheme = scheme;
			return this;
		}
		
		public Builder contentType(String contentType) {
			this.contentType = contentType;
			return this;
		}
		
		public Builder content(String content) {
			this.content = content;
			return this;
		}
		
		public Builder keyId(String keyId) {
			this.keyId = keyId;
			return this;
		}
		
		public MediaProtectionType type() {
			return type;
		}
		
		public String scheme() {
			return scheme;
		}
		
		public String contentType() {
			return contentType;
		}
		
		public String content() {
			return content;
		}
		
		public String keyId() {
			return keyId;
		}
	}
}