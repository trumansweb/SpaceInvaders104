package org.truman.sound;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Sound {
	
	private String ref = "src/sounds/sparo.wav";
	private InputStream in = null;
	private AudioStream as = null;
	
	public Sound(){	
		        
	}
	public void play(){
		try {
			in = new FileInputStream(ref);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			as = new AudioStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		AudioPlayer.player.start(as);
	}
}
