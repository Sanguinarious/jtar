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
import java.util.ArrayList;
import java.util.List;

public class TarReader implements AutoCloseable {
	RandomAccessFile f;
	List<aFile> files = new ArrayList<>();

	public TarReader(String filename) throws IOException  {
		this.f = new RandomAccessFile(filename, "r");
	}
	
	private boolean checkZeroBlock(byte[] data) {
		for(byte b:data)
			if(b != 0)
				return false;
		return true;
	}
	
	public aFile[] getRecords() throws IOException, aFileException {
		if(files.isEmpty()) {
			while(true) {
				try {
					byte[] temp = new byte[512];
					if(f.read(temp)==512) {
						if(!checkZeroBlock(temp)) {
							aFile header = new aFile(temp);
							header.setFilePosition(f.getFilePointer());
							f.seek(f.getFilePointer()+aFile.tarFilePadTotal(header.getSize()));
							files.add(header);
						} else
							break;
					}
				} catch (IOException e) {
					break; // End of file
				}
			}
			f.seek(0);
			return files.toArray(new aFile[0]);
		} else
			return files.toArray(new aFile[0]);
	}
	
	public byte[] getBytes(aFile header) throws aFileException, IOException {
		if(header.getFilePosition() == 0)
			throw new aFileException("File Position Not Set");
		byte[] ret = new byte[(int) header.getSize()];
		f.seek(header.getFilePosition());
		if(f.read(ret) != header.getSize())
			throw new aFileException("Couldn't read all "+header.getSize()+" Bytes");
		f.seek(0);
		return ret;
	}

	@Override
	public void close() throws IOException {
		f.close();
	}
}
