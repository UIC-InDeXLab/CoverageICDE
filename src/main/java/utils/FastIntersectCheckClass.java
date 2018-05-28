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
		
		// add a string pattern
		public native void add(@StdString String newPattern);
		
		// print num_bit_vecs
		public native void get_num_bit_vecs();
		
		// print num_patterns
		public native void get_num_patterns();

		// check if a selection of bit vectors intersect
		public native boolean intersect(@StdVector int[] indices);
	}

	public static void main(String[] args) {
		// Pointer objects allocated in Java get deallocated once they become
		// unreachable,
		// but C++ destructors can still be called in a timely fashion with
		// Pointer.deallocate()
		FastIntersectCheck l = new FastIntersectCheck(3);

		l.add(new int[]{1,0,1});
		for (int i = 0; i < 3; i++)
			l.add(new int[]{1,1,1});
		
		l.add(new int[]{0,0,1});

		System.out.println(l.intersect(new int[]{0,1,2}));
	}
}