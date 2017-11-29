/*
* @author Patricia Ferreira Lima
* @author Jorge Mauro Goncalves
* @author Paulo Victor de Oliveira Leal
*/

import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Classe responsavel pela leitura de todos os caracteres do arquivo de entrada e a devida associacao entre token e lexema  
 */

class AnalisadorLexico{

	/*
	 * Variaveis auxiliares
	 * linha       -> Acompanha o numero da linha atual, para printar em caso de ERRO
	 * estado      -> Inicia em 0 ate estado 17, sendo que o estado 17 e exclusivo para casos de ERRO
    * c           -> Variavel que realiza a leitura de cada caractere do arquivo de entrada
    * regLexAtual -> Instancia do tipo RegistroLexico, que auxilia na criacao dos registros de cada um dos tokens inseridos
    * lex         -> Variavel do tipo String, com base na atualizacao dessa variavel que formamos nosso lexema
	 */

    public static RegistroLexico regLexAtual;
    public static TabelaSimbolos ts;
    public static int linha = 1;
    public static int estado = 0;
    public static int c;
    public static String lex = "";

    public static ArrayList<RegistroLexico> registroLexico = new ArrayList<RegistroLexico>();
   
   /*
    * Metodo "principal" da classe, responsavel por iniciar a leitura do arquivo e tratar os possiveis erros
    */

    public void AnalisadorLex(String arquivo){
        try{

            PushbackInputStream leitor = new PushbackInputStream(new FileInputStream(arquivo));
            TokenAutomato(leitor);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Nao foi possivel abrir o arquivo!");
            System.exit(0);
        }
    }
   /*
    * Metodo que verifica se e fim de arquivo
    * @param d, caractere corrente
    * @return true ou false
    */

    public static boolean eFimArquivo(int d){
        return d == -1 ? true : false;
    }


    public static void TokenAutomato(PushbackInputStream leitor)throws Exception{
        //System.out.println("Entrei1");
        ts = new TabelaSimbolos();
        do{
            c = leitor.read();

            if(eFimArquivo(c) && estado == 0){
                ts.insere("", NumToken.T_EOF);
                regLexAtual = new RegistroLexico("", NumToken.T_EOF, "C_RESERVADA", "T_RESERVADO");
                regLexAtual.setLinha(linha);
                registroLexico.add(regLexAtual);
                break;
            }
            else if(eFimArquivo(c) && estado != 0 && estado != 15){
                estado = 16; // estado de erro
                System.out.println(linha + ":fim de arquivo nao esperado");
                System.exit(0);
                break;
            }
            else if(estado == 15 && !eFimArquivo(c)){
                estado = 0;
            }


         /*
          * Verifica se o caractere pertence ao alfabeto
          * 0x0D -> 13 Tabela ascii - Carriage return - Volta para inicio da linha
          * 0xA  -> 10 Tabela ascii - New Line - Quebra de Linha
          */



            if(Character.isLetterOrDigit(c) || c == '*' || c == ' ' || c == '_' || c == '.' || c == ',' || c == ';' || c == '&' ||
                    c == ':' || c == '(' || c == ')'  || c == '[' || c == ']' || c == '{' || c == '}' || c == '+' || c == '-'  ||
                    c == '\"' || c == '\'' || c == '/' || c == '%' || c == '^' || c == '@' || c == '!' || c == '?' || c == '>' ||
                    c == '<' || c == '=' || c == 0xA || c == 0x0D || c == '\t'){


                switch(estado){
                    case 0:

                        if(c == ' ' || c == 0xA || c == 0x0D || c == '\t'){
                            estado = 0;
                            if(c == 0xA) linha++;
                        }
                        else if(Character.isDigit(c) && c != '0'){
                            lex += (char)c;
                            estado = 14;
                        }
                        else if(c == '\"'){
                            lex +=  "" + (char)c;
                            estado = 12;
                        }
                        else if(c == '0'){
                            lex += (char)c;
                            estado = 7;
                        }
                        else if(c == '/'){
                            lex += (char)c;
                            estado = 1;
                        }
                        else if(c == '>'){
                            lex += (char)c;
                            estado = 4;
                        }
                        else if(c == '<'){
                            lex += (char)c;
                            estado = 5;
                        }
                        else if(Character.isLetter(c)){
                            lex += (char)c;
                            estado = 17;
                        }
                        else if(c == '_' || c == '.'){
                            lex += (char)c;
                            estado = 6;
                        }
                        else if(c == '\''){
                            lex +=  "" + (char)c;
                            estado = 10;
                        }
                        else if(c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '%' || c == ';' ||
                                c == ',' || c == '+' || c == '-' || c == '^' || c == '!' || c == '?' || c == '=' || c == '*'){
                            lex +=  "" + (char)c;
                            estado = 15;
                            ts.pesquisa(lex);
                            regLexAtual = new RegistroLexico(lex,ts.pesquisa(lex).getToken(), "C_RESERVADA", "T_RESERVADO");
                            regLexAtual.setLinha(linha);
                            registroLexico.add(regLexAtual);
                            lex = "";
                        }
                        else{
                            estado = 16; // estado de erro
                            System.out.println(linha + ":lexema nao identificado [" + lex + "]");
                            System.exit(0);
                            break;
                        }
                        break;

                    case 1:
                        if(c == '*'){
                            lex += (char)c;
                            estado = 2;
                        }
                        else{
                            estado = 15;
                            ts.pesquisa(lex);
                            regLexAtual = new RegistroLexico(lex,ts.pesquisa(lex).getToken(), "C_RESERVADA", "T_RESERVADO");
                            regLexAtual.setLinha(linha);
                            registroLexico.add(regLexAtual);
                            leitor.unread(c);
                            lex = "";
                        }
                        break;

                    case 2:
                        if(c != '*' && !eFimArquivo(c)){
                            if(c == 0xA) linha++;
                            lex += (char)c;
                            estado = 2;
                        }
                        else if(c == '*'){
                            lex += (char)c;
                            estado = 3;
                        }
                        else if(eFimArquivo(c)){
                            estado = 16;
                            System.out.println(linha + ":lexema nao identificado [" + lex + "]");
                            System.exit(0);

                        }
                        break;

                    case 3:

                        if(c == '*'){
                            lex += (char)c;
                            estado = 3;
                        }
                        else if(c != '/' && c!= '*'){
                            if(c == 0xA) linha++;
                            lex += (char)c;
                            estado = 2;
                        }
                        else {
                            lex += (char)c;
                            estado = 0;
                            lex = "";
                        }
                        break;

                    case 4:
                        if(c == '='){
                            lex += (char)c;
                            estado = 15;
                            ts.pesquisa(lex);
                            regLexAtual = new RegistroLexico(lex,ts.pesquisa(lex).getToken(), "C_RESERVADA", "T_RESERVADO");
                            regLexAtual.setLinha(linha);
                            registroLexico.add(regLexAtual);
                            lex = "";
                        }
                        else{
                            estado = 15;
                            ts.pesquisa(lex);
                            regLexAtual = new RegistroLexico(lex,ts.pesquisa(lex).getToken(), "C_RESERVADA", "T_RESERVADO");
                            regLexAtual.setLinha(linha);
                            registroLexico.add(regLexAtual);
                            leitor.unread(c);
                            lex = "";
                        }
                        break;

                    case 5:
                        if(c == '>'){
                            lex += (char)c;
                            estado = 15;
                            ts.pesquisa(lex);
                            regLexAtual = new RegistroLexico(lex,ts.pesquisa(lex).getToken(), "C_RESERVADA", "T_RESERVADO");
                            regLexAtual.setLinha(linha);
                            registroLexico.add(regLexAtual);
                            lex = "";
                        }
                        else if(c == '='){
                            lex += (char)c;
                            estado = 15;
                            ts.pesquisa(lex);
                            regLexAtual = new RegistroLexico(lex,ts.pesquisa(lex).getToken(), "C_RESERVADA", "T_RESERVADO");
                            regLexAtual.setLinha(linha);
                            registroLexico.add(regLexAtual);
                            lex = "";
                        }
                        else{
                            estado = 15;
                            ts.pesquisa(lex);
                            regLexAtual = new RegistroLexico(lex,ts.pesquisa(lex).getToken(), "C_RESERVADA", "T_RESERVADO");
                            regLexAtual.setLinha(linha);
                            registroLexico.add(regLexAtual);
                            leitor.unread(c);
                            lex = "";
                        }
                        break;

                    case 6:
                        if(Character.isDigit(c) || c == '_' || c == '.'){
                            lex += (char)c;
                            estado = 6;
                        }
                        else if(Character.isLetter(c)){
                            lex += (char)c;
                            estado = 17;
                        }
                        else{
                            estado = 16;
                            System.out.println(linha + ":lexema nao identificado [" + lex + "]");
                            System.exit(0);
                            break;
                        }
                        break;

                    case 17:

                        if(Character.isLetterOrDigit(c) || c == '_' || c == '.'){
                            lex += (char)c;
                            estado = 17;
                        }
                        else{
                            estado = 15;
                            if(ts.pesquisa1(lex)){
                                regLexAtual = ts.pesquisa(lex);
                            }
                            else{
                                if(ts.pesquisa(lex) == null){
                                    ts.insere(lex, NumToken.T_ID);
                                }
                                regLexAtual = new RegistroLexico(lex,ts.pesquisa(lex).getToken(), "C_RESERVADA", "T_RESERVADO");
                            }
                            regLexAtual.setLinha(linha);
                            registroLexico.add(regLexAtual);
                            leitor.unread(c);
                            lex = "";
                        }


                        break;

                    case 7:
                        if(c == 'x'){
                            lex += (char)c;
                            estado = 8;
                        }
                        else{
                            estado = 15;
                            regLexAtual = new RegistroLexico(lex,NumToken.T_CONSTANTE, "C_CONSTANTE", "T_INTEIRO");
                            regLexAtual.setLinha(linha);
                            registroLexico.add(regLexAtual);
                            leitor.unread(c);
                            lex = "";
                        }
                        break;

                    case 8:
                        if(c == '0' ||c == '1' ||c == '2' ||c == '3' ||c == '4' ||c == '5' ||c == '6' ||c == '7' ||
                                c == '8' ||c == '9' ||c == 'A' ||c == 'B' ||c == 'C' ||c == 'D' ||c == 'E' ||c == 'F' ||
                                c == 'a' ||c == 'b' ||c == 'c' ||c == 'd' ||c == 'e' ||c == 'f'){
                            lex += (char)c;
                            estado = 9;
                        }
                        else{
                            estado = 16;
                            System.out.println(linha + ":lexema nao identificado [" + lex + "]");
                            System.exit(0);
                            break;
                        }
                        break;

                    case 9:
                        if(c == '0' ||c == '1' ||c == '2' ||c == '3' ||c == '4' ||c == '5' ||c == '6' ||c == '7' ||
                                c == '8' ||c == '9' ||c == 'A' ||c == 'B' ||c == 'C' ||c == 'D' ||c == 'E' ||c == 'F' ||
                                c == 'a' ||c == 'b' ||c == 'c' ||c == 'd' ||c == 'e' ||c == 'f'){
                            lex += (char)c;
                            regLexAtual = new RegistroLexico(lex,NumToken.T_HEXA, "C_CONSTANTE", "T_CARACTERE");
                            regLexAtual.setLinha(linha);
                            registroLexico.add(regLexAtual);
                            estado = 15;
                            lex = "";
                        }
                        else{
                            estado = 16;
                            System.out.println(linha + ":lexema nao identificado [" + lex + "]");
                            System.exit(0);
                            break;
                        }
                        break;

                    case 10:
                        if(Character.isLetterOrDigit(c)){
                            lex += (char)c;
                            estado = 11;
                        }
                        else{
                            estado = 16;
                            System.out.println(linha + ":lexema nao identificado [" + lex + "]");
                            System.exit(0);
                            break;
                        }
                        break;

                    case 11:
                        if(c == '\''){
                            lex += (char)c;
                            estado = 15;
                            regLexAtual = new RegistroLexico(lex, NumToken.T_CARACTERE, "C_CONSTANTE", "T_CARACTERE");
                            regLexAtual.setLinha(linha);
                            ts.insere(lex, NumToken.T_CARACTERE);
                            registroLexico.add(regLexAtual);
                            lex = "";
                        }
                        else{
                            estado = 16;
                            System.out.println(linha + ":lexema nao identificado [" + lex + "]");
                            System.exit(0);
                            break;
                        }
                        break;

                    case 12:
                        if(c != '$' && c != '\"' && c != 0xA){
                            lex += (char)c;
                            estado = 12;
                        }
                        else if(c=='\"'){
                            lex += "" +(char)c;
                            estado = 13;
                        }
                        else{
                            estado = 16;
                            System.out.println(linha + ":lexema nao identificado [" + lex + "]");
                            System.exit(0);
                            break;
                        }
                        break;

                    case 13:
                        if(c == '\"'){
                            lex += (char)c;
                            estado = 12;
                        }
                        else{
                            estado = 15;
                            regLexAtual = new RegistroLexico(lex, NumToken.T_STRING, "C_CONSTANTE", "T_STRING");
                            regLexAtual.setLinha(linha);
                            ts.insere(lex, NumToken.T_STRING);
                            registroLexico.add(regLexAtual);
                            lex = "";
                            leitor.unread(c);
                        }
                        break;

                    case 14:
                        if(Character.isDigit(c)){
                            lex += (char)c;
                            estado = 14;
                        }
                        else{
                            estado = 15;
                            if(ts.pesquisa(lex) == null){
                                regLexAtual = new RegistroLexico(lex,NumToken.T_CONSTANTE, "C_CONSTANTE", "T_INTEIRO");
                                regLexAtual.setLinha(linha);
                                registroLexico.add(regLexAtual);
                                leitor.unread(c);
                                lex = "";
                            }
                            else{
                                ts.pesquisa(lex);
                            }
                        }
                        break;
                }
            }
            else{
                System.out.println(linha + ":caractere invalido [" + (char)c + "]");
                System.exit(0);
            }
        }while(estado != 15 || estado != 16);
    }
}
