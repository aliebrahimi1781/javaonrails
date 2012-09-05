package me.jor.codereview.pmd;

import net.sourceforge.pmd.PMD;

public class JavaCheckExecutor {
	public static void check(String[] args){
		PMD.main(args);
	}
}
