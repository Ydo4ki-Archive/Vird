import com.ydo4ki.vird.DefaultEnv;
import com.ydo4ki.vird.Vird;
import com.ydo4ki.vird.VirdSrc;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;

import java.io.File;

/**
 * @since 7/5/2025 7:54 PM
 * 
 */
public class Playground {
	public static void main(String[] args) throws LangValidationException {
		Vird.establishValidationErrorsHandler();
		VirdSrc vird = VirdSrc.fromFile(new File("vird/playground.vird"));
		System.out.println(Vird.run(vird, new Scope(new DefaultEnv())));
	}
}
