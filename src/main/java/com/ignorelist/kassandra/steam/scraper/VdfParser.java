/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.IOException;
import java.io.InputStream;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Andreas Schnaiter <rc.poison@gmail.com>
 */
public class VdfParser {

	public static VdfRoot parse(InputStream iStream) throws RecognitionException, IOException {
		ANTLRInputStream ais=new ANTLRInputStream(iStream);
		ValveTokenLexer lexer=new ValveTokenLexer(ais);
		SloppyParser parser=new SloppyParser(new CommonTokenStream(lexer));
		VdfRoot root=parser.main();
		return root;
	}

}
