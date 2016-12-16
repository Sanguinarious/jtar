/*  
  Copyright (C) 2016 William Welna (wwelna@occultusterra.com)
  
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
*/

package com.occultusterra.jtar;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class aFile {
	private String name = "";
	private String name_prefix = "";
	private PermMask pm = new PermMask();
	private String owner="", group="";
	private long uid=0, gid=0;
	private long timestamp=0;
	private long size=0;
	private String link_name="";
	private byte link_indicator='0';
	private long filePosition=0;
	
	private boolean ustar_extend=true;
	byte[] ext = null; // 255 bytes of extended data
	
	public static final byte LF_OLDNORMAL = '\0';
	public static final byte LF_NORMAL =    '0';
	public static final byte LF_LINK =      '1';
	public static final byte LF_SYMLINK =   '2';
	public static final byte LF_CHR =       '3';
	public static final byte LF_BLK =       '4';
	public static final byte LF_DIR =       '5';
	public static final byte LF_FIFO =      '6';
	public static final byte LF_CONTIG =    '7';
	
	private static final String MAGIC_USTAR = "ustar  ";
	private static final String MAGIC_GNUTAR = "GNUtar ";
	
	public static long tarFilePad(long size) {
		return 512-(size%512);
	}
	
	public static long tarFilePadTotal(long size) {
		return ((size/512)*512)+512;
	}
	
	private static long tarChecksum(long start, byte[] tarHeader) {
		long checksum=start;
		for(byte b: tarHeader)
			checksum += (b&0xff);
		return checksum;
	}
	
	public aFile(byte[] tarHeader) throws UnsupportedEncodingException, aFileException {
		readTarHeader(tarHeader);
	}
	
	public aFile() {
		
	}

	public void setUstarExtension(boolean b) {
		this.ustar_extend=b;
	}
	
	public boolean getUstarExtension() {
		return this.ustar_extend;
	}
	
	public void setExt(byte[] b) {
		this.ext = Arrays.copyOf(b, 255);
	}
	
	public byte[] getExt() {
		return Arrays.copyOf(this.ext, this.ext.length);
	}
	
	public void setFileName(String s) throws aFileException {
		if(s.getBytes().length > 99)
			throw new aFileException("Longer than 99 bytes");
		else
			this.name = s;
	}
	
	public String getFileName() {
		return this.name;
	}
	
	public void setPermisions(PermMask m) {
		this.pm.setMask(m.getMask());
	}
	
	public void setPermisions(String m) {
		this.pm.setOctal(m);
	}
	
	public PermMask getPermisions() {
		return new PermMask(pm.getMask());
	}
	
	public void setOwner(String s) throws aFileException {
		if(s.getBytes().length > 31)
			throw new aFileException("Longer than 31 bytes");
		else
			this.owner = s;
	}
	
	public String getOwner() {
		return this.owner;
	}
	
	public void setGroup(String s) throws aFileException {
		if(s.getBytes().length > 31)
			throw new aFileException("Longer than 31 bytes");
		else
			this.group = s;
	}
	
	public String getGroup() {
		return this.group;
	}
	
	public void setUID(long l) {
		this.uid = l;
	}
	
	public long getUID() {
		return this.uid;
	}
	
	public void setGID(long l) {
		this.gid = l;
	}
	
	public long getGID() {
		return this.gid;
	}
	
	public void setTimestamp(long l) {
		this.timestamp = l;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	public void setSize(long l) {
		this.size = l;
	}
	
	public long getSize() {
		return this.size;
	}
	
	public void setLinkName(String s) throws aFileException {
		if(s.getBytes().length > 99)
			throw new aFileException("Longer than 99 bytes");
		else
			this.link_name = s;
	}
	
	public String getLinkName() {
		return this.link_name;
	}
	
	public void setLinkIndicator(byte i) throws aFileException {
		if((i != 0) && (i>'7' || i<'0'))
			throw new aFileException("Invalid Link Indicator");
		else
			this.link_indicator = i;
	}
	
	public byte getLinkIndicator() {
		return this.link_indicator;
	}
	
	void setFilePosition(long l) {
		this.filePosition=l;
	}
	
	long getFilePosition() {
		return this.filePosition;
	}
	
	private String doZeros(byte[] b) {
		StringBuilder temp = new StringBuilder();
		for(byte a:b)
			if(a == 0)
				break;
			else
				temp.append((char) a);
		return temp.toString();
	}
	
	private long checkLong(String s) throws aFileException {
		if(!s.matches("[0-7]{6,11}"))
			throw new aFileException("Not a Octacal String");
		else
			return Long.parseLong(s, 8);
	}
	
	public void readTarHeader(byte[] tarHeader) throws UnsupportedEncodingException, aFileException {
		if(tarHeader.length != 512)
			throw new aFileException("Invalid Header Size, Expects 512 bytes");
		long checksum=0, checksum_calculated=0;
		ByteBuffer bb = ByteBuffer.wrap(tarHeader);
		ByteBuffer bb_temp;
		byte[] temp;
		String magic;
		temp = new byte[99]; bb.get(temp, 0, 99); setFileName(doZeros(temp));
		temp = new byte[7]; bb.position(100); bb.get(temp, 0, 7); this.pm = new PermMask(doZeros(temp));
		temp = new byte[7]; bb.position(108); bb.get(temp, 0, 7); setUID(checkLong(doZeros(temp)));
		temp = new byte[7]; bb.position(116); bb.get(temp, 0, 7); setGID(checkLong(doZeros(temp)));
		temp = new byte[11]; bb.position(124); bb.get(temp, 0, 11); setSize(checkLong(doZeros(temp)));
		temp = new byte[11]; bb.position(136); bb.get(temp, 0, 11); setTimestamp(checkLong(doZeros(temp)));
		this.link_indicator = bb.get(156);
		temp = new byte[99]; bb.position(157); bb.get(temp, 0, 99); setLinkName(doZeros(temp));
		temp = new byte[7]; bb.position(257); bb.get(temp, 0, 7); magic = doZeros(temp);
		if(magic.equals(MAGIC_USTAR)|| magic.equals(MAGIC_GNUTAR)) {
			temp = new byte[31]; bb.position(265); bb.get(temp, 0, 31); setOwner(doZeros(temp));
			temp = new byte[31]; bb.position(297); bb.get(temp, 0, 31); setGroup(doZeros(temp));
			temp = new byte[154]; bb.position(345); bb.get(temp, 0, 154); this.name_prefix = doZeros(temp);
		} else {
			temp = new byte[255]; bb.position(257); bb.get(temp, 0, 255); setExt(temp);
		}
		temp = new byte[6]; bb.position(148); bb.get(temp, 0, 6); checksum = checkLong(doZeros(temp));
		bb_temp = ByteBuffer.wrap(Arrays.copyOf(tarHeader, tarHeader.length));
		bb_temp.position(148); bb_temp.put(new String("        ").getBytes(), 0, 8);
		checksum_calculated = tarChecksum(0, bb_temp.array());
		if(bb.get(155) != 0x20 || checksum != checksum_calculated)
			throw new aFileException("Bad Checksum");
	}

	public byte[] getTarHeader() throws UnsupportedEncodingException {
		ByteBuffer bb = ByteBuffer.allocate(512);
		bb.put(Arrays.copyOfRange(name.getBytes("UTF-8"), 0, 100), 0, 100);
		bb.position(100); bb.put(pm.getOctalTar().getBytes("UTF-8"), 0, 7);
		bb.position(108); bb.put(String.format("%07o", uid).getBytes("UTF-8"), 0, 7);
		bb.position(116); bb.put(String.format("%07o", gid).getBytes("UTF-8"), 0, 7);
		bb.position(124); bb.put(String.format("%011o", size).getBytes("UTF-8"), 0, 11);
		bb.position(136); bb.put(String.format("%011o", timestamp).getBytes("UTF-8"), 0, 11);
		bb.position(157); bb.put(Arrays.copyOfRange(link_name.getBytes("UTF-8"), 0, 100), 0, 100);
		bb.put(156, link_indicator);
		if(ustar_extend) {
			bb.position(257); bb.put(MAGIC_USTAR.getBytes("UTF-8"), 0, 7);
			bb.position(265); bb.put(Arrays.copyOfRange(owner.getBytes("UTF-8"), 0, 32), 0, 32);
			bb.position(297); bb.put(Arrays.copyOfRange(group.getBytes("UTF-8"), 0, 32), 0, 32);
			bb.position(345); bb.put(Arrays.copyOfRange(name_prefix.getBytes("UTF-8"), 0, 155), 0, 155);
		} else if(ext != null) {
			bb.position(257);
			bb.put(ext, 0, 255);
		}
		bb.position(148); bb.put(String.format("%06o", tarChecksum(0x20*8, bb.array())).getBytes("UTF-8"), 0, 6);
		bb.put(155, (byte) 0x20);
		return bb.array();
	}
}
