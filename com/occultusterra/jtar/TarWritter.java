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

import java.io.IOException;
import java.io.RandomAccessFile;

public class TarWritter implements AutoCloseable {
	RandomAccessFile f;
	
	public TarWritter(String filename) throws IOException {
		this.f = new RandomAccessFile(filename, "rw");
		f.seek(f.length());
	}
	
	public void writeRecord(aFile header, byte[] data) throws IOException {
		if(header.getLinkIndicator() != aFile.LF_DIR) header.setSize(data.length);
		f.write(header.getTarHeader(), 0, 512);
		if(header.getLinkIndicator() != aFile.LF_DIR) {
			byte[] pad = new byte[(int) aFile.tarFilePad(data.length)];
			f.write(data, 0, data.length);
			f.write(pad, 0, pad.length);
		}
	}

	@Override
	public void close() throws IOException {
		f.close();
	}

}
