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

public class PermMask {
	private short mask = 0644; // Default mask, u+rw, go+r
	
	final public short USER_READ =         0b0000000100000000;
	final public short USER_WRITE =        0b0000000010000000;
	final public short USER_EXECUTE =      0b0000000001000000;
	
	final public short GROUP_READ =        0b0000000000100000;
	final public short GROUP_WRITE =       0b0000000000010000;
	final public short GROUP_EXECUTE =     0b0000000000001000;
	
	final public short OTHERS_READ =       0b0000000000000100;
	final public short OTHERS_WRITE =      0b0000000000000010;
	final public short OTHERS_EXECUTE =    0b0000000000000001;
	
	public PermMask(short mask) {
		setMask(mask);
	}
	
	public PermMask(String mask) {
		setOctal(mask);
	}
	
	public PermMask() {
		this.mask = 0644;
	}
	
	public void setMask(short mask) {
		this.mask = mask;
	}
	
	public short getMask() {
		return this.mask;
	}
	
	public void setBits(short bits) {
		this.mask |= bits;
	}
	
	public void clearBits(short bits) {
		this.mask &= ~bits;
	}
	
	public String getOctal() {
		return String.format("%o", this.mask);
	}
	
	public String getOctalTar() {
		return String.format("%07o", this.mask);
	}
	
	public void setOctal(String m) {
		if(m.matches("[0-7]{3,4}"))
			this.mask = Short.parseShort(m, 8);
	}
	
	public boolean userCanRead() {
		if(this.USER_READ == (this.mask&this.USER_READ))
			return true;
		else
			return false;
	}
	
	public boolean userCanWrite() {
		if(this.USER_WRITE == (this.mask&this.USER_WRITE))
			return true;
		else
			return false;
	}
	
	public boolean userCanExecute() {
		if(this.USER_EXECUTE == (this.mask&this.USER_EXECUTE))
			return true;
		else
			return false;
	}
	
	public boolean groupCanRead() {
		if(this.GROUP_READ == (this.mask&this.GROUP_READ))
			return true;
		else
			return false;
	}
	
	public boolean groupCanWrite() {
		if(this.GROUP_WRITE == (this.mask&this.GROUP_WRITE))
			return true;
		else
			return false;
	}
	
	public boolean groupCanExecute() {
		if(this.GROUP_EXECUTE == (this.mask&this.GROUP_EXECUTE))
			return true;
		else
			return false;
	}
	
	public boolean othersCanRead() {
		if(this.OTHERS_READ == (this.mask&this.OTHERS_READ))
			return true;
		else
			return false;
	}
	
	public boolean othersCanWrite() {
		if(this.OTHERS_WRITE == (this.mask&this.OTHERS_WRITE))
			return true;
		else
			return false;
	}
	
	public boolean othersCanExecute() {
		if(this.OTHERS_EXECUTE == (this.mask&this.OTHERS_EXECUTE))
			return true;
		else
			return false;
	}
	
}
