package com.gjs.developresponsity.video.tools.log;

import android.util.Log;

//import com.feinno.webrtc.AvSession;

import java.util.List;

public class Trace {
	public static boolean isOpen = true;
	public static void T() {
		if(isOpen){
			T ("", 2);
		}
		
	}

	public static void T2() {
		if (isOpen) {
			T ("<---", 3);
			T ("", 2);
		}
	}

	public static void T2(String l) {
		if (isOpen) {
			T (l, 2);
			T ("<---", 3);
		}
	}

	public static void T(String l) {
		if (isOpen) {
			T (l, 2);
		}
	}

	public static void T(long l) {
		if (isOpen) {
			
			T (Long.toString(l), 2);
		}
	}
	
	////////////// type
	public static void F() {
		if (isOpen) {
			
			T ("", 2);
		}
	}

	public static void F2() {
		if (isOpen) {
			
			T ("", 2);
			T ("<---", 3);
		}
	}

	public static void F2(String l) {
		if (isOpen) {
			
			T (l, 2);
			T ("<---", 3);
		}
	}

	public static void F(String l) {
		if (isOpen) {
			
			T (l, 2);
		}
	}

	public static void F(long l) {
		if (isOpen) {
			
			T (Long.toString(l), 2);
		}
	}
	
	private static String listToString (List<Long> members) {
		if (members == null) {
			return "";
		}
		StringBuffer toStringBuffer = new StringBuffer();
		for (Long m : members) {
			toStringBuffer.append(m);
			toStringBuffer.append(" ");
		}
		return toStringBuffer.toString();
		
	}
	
	public static void T (List<Long> members) {
		if (isOpen) {
			
			T (listToString (members), 2);
		}
	}
	
//	public static void T(AvSession s) {
//		if (s == null) {
//			return;
//		}
//		T ("SessionKey:" + s.GetSessionKey()
//				+ " type:" + s.getSessionType()
//				+ " user:" + s.getUserId()
//				+ " address:" + s.getAddress()
//				+ " members:" + listToString (s.mMemberList)
//				+ " adds:" + listToString (s.mAddedList)
//				, 2);
//	}
	
	public static void T(String l, int level) {
		if (isOpen) {
			StackTraceElement[] stacks = (new Exception()).getStackTrace();
			if (stacks == null || stacks.length <= level) {
				return;
			}
			StackTraceElement traceElement = stacks[level];
			if (traceElement == null) {
				return;
			}
			String cls = traceElement.getFileName();
			if (cls == null || cls.length() <= 5) {
				return;
			}
			
			String method = traceElement.getMethodName();
			if (method == null) {
				return;
			}
			
			StringBuffer toStringBuffer = new StringBuffer("[").append(method).append("]").append(
			" " ).append(l==null ? "" : l); 
			String log = toStringBuffer.toString() + " ";
			Log.e(cls.substring(0, cls.length() - 5), log);
		}
		
	}

	public static boolean isOpen() {
		return isOpen;
	}

	public static void setOpen(boolean isOpen) {
		Trace.isOpen = isOpen;
	}
}
