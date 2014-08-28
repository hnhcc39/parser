package com.internal.util.json;

public class StdoutStreamErrorListener extends BufferErrorListener {
    
    @Override
	public void end() {
        System.out.print(buffer.toString());
    }
}
