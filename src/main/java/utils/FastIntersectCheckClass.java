package utils;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

@Platform(include = "FastIntersectCheck.h")
@Namespace("Beaver")
public class FastIntersectCheckClass {
	public static class FastIntersectCheck extends Pointer {
		static {
			Loader.load();
		}
		public FastIntersectCheck(int numOfBitVecs) {
			allocate(numOfBitVecs);
		}
		private native void allocate(int n); 

		// to add a pattern
		public native void add(int[] newVal);
		public native void add(@StdString String newPattern);

		// check if a selection of bit vectors intersect
		public native boolean intersect(@StdVector int[] indices);
	}

	public static void main(String[] args) {
		// Pointer objects allocated in Java get deallocated once they become
		// unreachable,
		// but C++ destructors can still be called in a timely fashion with
		// Pointer.deallocate()
		FastIntersectCheck l = new FastIntersectCheck(3);

		l.add(new int[]{0,1,0});
		l.add(new int[]{1,0,0});
		l.add(new int[]{1,1,1});
		System.out.println(l.intersect(new int[]{0,2}));
	}
}