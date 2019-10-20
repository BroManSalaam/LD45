package com.connectike.util;

import java.util.ArrayList;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class PlayerInputManager implements InputProcessor {
	
	public ArrayList<Integer> keysDown = new ArrayList<Integer>();
	public boolean isW;
	public boolean isA;
	public boolean isS;
	public boolean isD;
	public boolean isSpace;
	public boolean isShift;
	
	public int lastClickX = 0;
	public int lastClickY = 0;
	
	public boolean scrollUp;
	public boolean scrollDown;
	
	/**
	 * Keeps track of all the user's keyboard input.
	 * 
	 * Usage:
	 * PlayerInputManager.isW   to see if W is pressed
	 * 
	 */
	public PlayerInputManager() {
		
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		if(!keysDown.contains(keycode)) {
			keysDown.add(keycode);
		}
		if(keycode == Input.Keys.W) {
			isW = true;
		}
		if(keycode == Input.Keys.A) {
			isA = true;
		}
		if(keycode == Input.Keys.S) {
			isS = true;
		}
		if(keycode == Input.Keys.D) {
			isD = true;
		}
		
		// atk buffering means you cant attack
		if(keycode == Input.Keys.SPACE) {
			isSpace = true;
		}
		if(keycode == Input.Keys.SHIFT_LEFT) {
			isShift = true;
		}
		
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		
		//removeInput(keycode);
		if(keycode == Input.Keys.W) {
			isW = false;
		}
		if(keycode == Input.Keys.A) {
			isA = false;
		}
		if(keycode == Input.Keys.S) {
			isS = false;
		}
		if(keycode == Input.Keys.D) {
			isD = false;
		}
		if(keycode == Input.Keys.SPACE) {
			isSpace = false;
		}
		if(keycode == Input.Keys.SHIFT_LEFT) {
			isShift = false;
		}
		
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		lastClickX = screenX;
		lastClickY = screenY;
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		
		if(amount == 1) {
			scrollUp = true;
		}
		if(amount == -1) {
			scrollDown = true;
		}
		
		return false;
	}
	
	/**
	 * After an input has been processed, it can be removed from the list
	 * in order to prevent double processing
	 * 
	 * @param keyCode
	 * integer code for the key pressed
	 * 
	 * @author seth
	 */
	public void removeInput(int keyCode) {
		keysDown.remove(keysDown.indexOf(keyCode));
	}
	
	
}
