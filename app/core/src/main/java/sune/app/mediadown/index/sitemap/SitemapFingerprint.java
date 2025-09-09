package sune.app.mediadown.index.sitemap;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import sune.app.mediadown.index.Shared;

public final record SitemapFingerprint(long count, String hxor, String hsum) {
	
	private static final VarHandle LONG_BE
		= MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
	
	public SitemapFingerprint(long count, String hxor, String hsum) {
		this.count = count;
		this.hxor = Objects.requireNonNull(hxor);
		this.hsum = Objects.requireNonNull(hsum);
	}
	
	private static final long bytesAsLong(byte[] buf, int off) {
		return (long) LONG_BE.get(buf, off);
	}
	
	private static final String toHexString(long hi, long lo) {
		return String.format("%016x%016x", hi, lo);
	}
	
	public static final SitemapFingerprint compute(Sitemap sitemap) throws IOException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch(NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex); // Should not happen
		}
		
		long count = 0L;
		long hxorHi = 0L, hxorLo = 0L;
		long hsumHi = 0L, hsumLo = 0L;
		
		CharsetEncoder encoder = Shared.CHARSET.newEncoder();
		ByteBuffer buf = null;
		byte[] digest = new byte[32]; // SHA-256 output
		
		for(SitemapUrlEntry entry : sitemap) {
			String loc = entry.loc();
			String lastmod = entry.lastmod();
			
			int maxLen = (int) (
				// Reduce the memory by doing 2 update operations of the message digest,
				// thus the required number of characters is the maximum length of the inputs.
				Math.max(loc.length(), lastmod.length()) * encoder.maxBytesPerChar()
			);
			
			if(buf == null || buf.capacity() < maxLen) {
				// Allocate a little more
				int newLen = (int) (1.5 * (Math.floor(maxLen / 1.5) + 1));
				buf = ByteBuffer.allocate(newLen);
			}
			
			encoder.reset();
			encoder.encode(CharBuffer.wrap(loc), buf, true);
			
			buf.flip();
			md.update(buf);
			buf.clear();
			
			encoder.reset();
			encoder.encode(CharBuffer.wrap(lastmod), buf, true);
			
			buf.flip();
			md.update(buf);
			buf.clear();
			
			try {
				md.digest(digest, 0, digest.length);
			} catch(DigestException ex) {
				throw new IOException(ex);
			}
			
			long hi = bytesAsLong(digest, 0);
			long lo = bytesAsLong(digest, 8);
			
			// XOR accumulator (order-independent)
			hxorHi ^= hi;
			hxorLo ^= lo;
			
			// SUM accumulator mod 2^128 (order-independent)
			long newLo = hsumLo + lo;
			long carry = Long.compareUnsigned(newLo, lo) < 0 ? 1L : 0L;
			hsumLo  = newLo;
			hsumHi += hi + carry;
			
			++count;
		}
		
		return new SitemapFingerprint(
			count,
			toHexString(hxorHi, hxorLo),
			toHexString(hsumHi, hsumLo)
		);
	}
}
