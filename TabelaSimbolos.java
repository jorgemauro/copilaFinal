/*
* @author Patricia Ferreira Lima
* @author Jorge Mauro Goncalves
* @author Paulo Victor de Oliveira Leal
*/

import java.util.Map;
import java.util.HashMap;

class TabelaSimbolos{
	private Map<String,RegistroLexico> simbolos;
// HashMap de Simbolos, associa um lexema a um NumToken(enum de tokens)
	public TabelaSimbolos(){
		simbolos = new HashMap<String,RegistroLexico>();

		simbolos.put("="     	, new RegistroLexico("="		   ,NumToken.T_IGUAL, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("("     	, new RegistroLexico("("		   ,NumToken.T_ABREPAR, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put(")"     	, new RegistroLexico(")"		   ,NumToken.T_FECHAPAR, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("<"     	, new RegistroLexico("<"		   ,NumToken.T_MENOR, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put(">"     	, new RegistroLexico(">"		   ,NumToken.T_MAIOR, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("<>"     	, new RegistroLexico("<>"		   ,NumToken.T_DIFERENTE, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put(">="     	, new RegistroLexico(">="		   ,NumToken.T_MAIORIGUAL, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("<="     	, new RegistroLexico("<="		   ,NumToken.T_MENORIGUAL, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put(","     	, new RegistroLexico(","		   ,NumToken.T_VIRGULA, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("+"     	, new RegistroLexico("+"		   ,NumToken.T_MAIS, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("-"     	, new RegistroLexico("-"		   ,NumToken.T_MENOS, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("*"     	, new RegistroLexico("*"		   ,NumToken.T_ASTERISCO, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("/"     	, new RegistroLexico("/"		   ,NumToken.T_BARRA, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put(";"     	, new RegistroLexico(";"		   ,NumToken.T_PONTOVIRGULA, 	"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("{"     	, new RegistroLexico("{"		   ,NumToken.T_ABRECHAVE, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("}"     	, new RegistroLexico("}"		   ,NumToken.T_FECHACHAVE, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("%"     	, new RegistroLexico("%"		   ,NumToken.T_PORCENTO, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("["     	, new RegistroLexico("["		   ,NumToken.T_ABRECOLCHETE, 	"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("]"     	, new RegistroLexico("]"		   ,NumToken.T_FECHACOLCHETE, 	"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("const"	, new RegistroLexico("const"	   ,NumToken.T_CONST, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("var"		, new RegistroLexico("var"		   ,NumToken.T_VAR, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("integer"	, new RegistroLexico("integer"	   ,NumToken.T_INTEGER, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("char"		, new RegistroLexico("char"		   ,NumToken.T_CHAR, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("for"		, new RegistroLexico("for"		   ,NumToken.T_FOR, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("if"		, new RegistroLexico("if"		   ,NumToken.T_IF, 				"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("else"		, new RegistroLexico("else"		   ,NumToken.T_ELSE, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("and"		, new RegistroLexico("and"		   ,NumToken.T_AND, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("or"		, new RegistroLexico("or"		   ,NumToken.T_OR, 				"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("to"		, new RegistroLexico("to"		   ,NumToken.T_TO, 				"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("then"		, new RegistroLexico("then"		   ,NumToken.T_THEN, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("readln"	, new RegistroLexico("readln"	   ,NumToken.T_READLN, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("step"		, new RegistroLexico("step"		   ,NumToken.T_STEP, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("write"	, new RegistroLexico("write"	   ,NumToken.T_WRITE, 			"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("writeln"	, new RegistroLexico("writeln"	   ,NumToken.T_WRITELN, 		"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("do"		, new RegistroLexico("do"		   ,NumToken.T_DO, 				"C_RESERVADA", "T_RESERVADO"));
		simbolos.put("not"		, new RegistroLexico("not"		   ,NumToken.T_NOT, 			"C_RESERVADA", "T_RESERVADO"));
	}


	public void atualizaClasse(String lexema, String classe){
		lexema = lexema.toLowerCase();
		simbolos.get(lexema).setClasse(classe);
	}

	public void atualizaTipo(String lexema, String tipo){
		lexema = lexema.toLowerCase();
		simbolos.get(lexema).setTipo(tipo);
	}
   
   //Funcao Utilizada no AnalisadorSemantico
	public RegistroLexico insere(String lexema, NumToken token, String classe, String tipo){
		lexema = lexema.toLowerCase();
		simbolos.put(lexema, new RegistroLexico(lexema, token, classe, tipo));
		return simbolos.get(lexema);
	}
   
	//Funcao Utilizada no AnalisadorLexico para tudo que e diferente de ID
	public RegistroLexico insere(String lexema, NumToken token, String tipo){
		lexema = lexema.toLowerCase();
		simbolos.put(lexema, new RegistroLexico(lexema, token, tipo));
		return simbolos.get(lexema);
	}

	//Funcao Utilizada no AnalisadorLexico para ID
   	public RegistroLexico insere(String lexema, NumToken token){
		lexema = lexema.toLowerCase();
		simbolos.put(lexema, new RegistroLexico(lexema, token));
		return simbolos.get(lexema);
   	}

	/*
  	* Primeira Funcao da Tabela de Simbolos TP1
  	* Verifica se o lexema existe na Tabela de Simbolos
  	*/
  	public RegistroLexico pesquisa(String lexema){
  		lexema = lexema.toLowerCase();
      	boolean numeroToken = simbolos.containsKey(lexema);     
      	if(numeroToken){
         	return simbolos.get(lexema);
      	}
      	else{
         	return null;
      	}
   	}

   	public boolean pesquisa1(String lexema){
		lexema = lexema.toLowerCase();
		boolean numeroToken = simbolos.containsKey(lexema);
		return numeroToken;
   	}
}