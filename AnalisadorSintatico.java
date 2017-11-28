/*
* @author Patricia Ferreira Lima
* @author Jorge Mauro Goncalves
* @author Paulo Victor de Oliveira Leal
*/

import java.io.*;
import java.util.Scanner;

public class AnalisadorSintatico{
   public static NumToken token_atual;
   public static int contRegLex = 0;
   public static AnalisadorLexico al = new AnalisadorLexico();
   public static int tamanhoArrayList = al.registroLexico.size()-1;
   public static NumToken token_esperado;
   public static int tamanhoVetor;

   private static int memoria;
   private static int temporarios;
   private static StringBuilder codigo;

   	/*
    * Inicia a chamada ao AnalisadorLexico passando o nome do arquivo previamente informado pelo usuario
    * Inicia a chamada do Procedimento Principal S()
    */
   public static void main (String [] args){
      try{
         memoria = 4000;
    	   temporarios = 0;
    	   codigo = new StringBuilder(); 
         Scanner entrada = new Scanner(System.in);
         System.out.println("Digite o nome do arquivo com sua extensao: ");
         String nomArq = entrada.next();
         al.AnalisadorLex(nomArq);
         
         //for(int i = 0; i < al.registroLexico.size(); i++) System.out.println("elemento: " + al.registroLexico.get(i).getToken());
         
         S();
         System.out.println("debug");
         if(al.eFimArquivo(al.c) == false){
            System.out.println("ERRO");
            System.exit(0);
         } 

         PrintWriter writer = new PrintWriter(nomArq+".assemble");
         writer.println(codigo.toString());
         writer.flush();
         writer.close();

      } catch(IndexOutOfBoundsException | IOException iooe){
          System.out.println(iooe);
      }
   }

   /*
    * Metodo responsavel pela verificacao do token
    * @param token_esperado, token pre-determinado pela gramatica
    */
   public static void casaToken(NumToken token_esperado){
      if(contRegLex < tamanhoArrayList){
         token_atual = al.registroLexico.get(contRegLex).getToken();
         if(token_atual != token_esperado){
            System.out.println("ERRO");
            System.exit(0);
         } 
      }
   }
   //S: {DEC}+ {COMANDOS}+
   public static void S(){
      codigo.append("sseg SEGMENT STACK               ;inicio seg. pilha\n");
      codigo.append("\t\tbyte 4000h DUP(?)                  ;inicio seg. pilha\n");
      codigo.append("sseg ENDS                  ;fim seg. pilha\n\n");

      codigo.append("dseg SEGMENT PUBLIC              ;inicio seg. dados\n");
      codigo.append("\t\tbyte 4000h DUP(?)                  ;temporarios\n");
      codigo.append("               ;definicioes de variaveis e constantes\n");

      token_atual = al.registroLexico.get(contRegLex).getToken();
      do{
         if(token_atual == NumToken.T_VAR)	{	decVar();	}
         else if(token_atual == NumToken.T_CONST)	{	decConstEscalar();	}
         else{
            System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
            break;
         }
      }while(token_atual == NumToken.T_VAR || token_atual == NumToken.T_CONST);
          
      codigo.append("dseg ENDS                  ;fim seg.dados\n\n");
      
      codigo.append("cseg SEGMENT PUBLIC              ;inicio seg. codigo\n");
      codigo.append("\t\tASSUME CS:cseg, DS:dseg\n\n");
      
      codigo.append("_strt:               ;inicio do programa\n");
      codigo.append("\t\tmov ax, dseg\n");
      codigo.append("\t\tmov ds, ax\n");

      do{
         comandos();
      }while(token_atual == NumToken.T_WRITE || token_atual == NumToken.T_WRITELN || token_atual == NumToken.T_ID || token_atual == NumToken.T_FOR || token_atual == NumToken.T_IF || token_atual == NumToken.T_PONTOVIRGULA || token_atual == NumToken.T_READLN);
   
      codigo.append("\t\tmov ah, 4Ch\n");
      codigo.append("\t\tint 21h\n");
      codigo.append("cseg ENDS\n");
      codigo.append("END _strt\n");

      if(token_atual == NumToken.T_EOF){
         token_esperado = NumToken.T_EOF;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex-1).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
         System.exit(0);
      }
   
   }

   //DEC_VAR: var {( integer | char ) id ['[' constante ']'] { ,id ['[' constante ']'] [ = VALOR ] }* ;}+
   public static void decVar(){
      //POG
       RegistroLexico rl;
       if(contRegLex>0) {
           rl = al.ts.pesquisa(al.registroLexico.get(contRegLex - 1).getLexema());
       } else if(contRegLex==0){
            rl = al.ts.pesquisa(al.registroLexico.get(contRegLex).getLexema());
       }else {
           System.exit(0);
       }
            int flag = -1;
      if(token_atual == NumToken.T_VAR){
         token_esperado = NumToken.T_VAR;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }
   
      do{
         if(token_atual == NumToken.T_INTEGER){
            token_esperado = NumToken.T_INTEGER;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 37
            flag = 0;
         }
         else if(token_atual == NumToken.T_CHAR){
            token_esperado = NumToken.T_CHAR;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 38
            flag = 1;
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
            break;
         }
      
         if(token_atual == NumToken.T_ID){
            token_esperado = NumToken.T_ID;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();

            rl = al.ts.pesquisa(al.registroLexico.get(contRegLex-1).getLexema());
            //Acao 39
            if(rl.getClasse() != null){
               System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":classe de identificador incompativel [" +  al.registroLexico.get(contRegLex-1).getLexema() + "]");
               System.exit(0);
            }
            else{
               rl.setClasse("C_VARIAVEL");
               if(flag == 0){
                  rl.setTipo("T_INTEIRO");

                  String mem = proximaPosicaoMemoria("T_INTEIRO", 0);
                  codigo.append("sword ?              ; var inteira em "+mem+ "\n");
                  rl.setPosicaoMemoria(mem);
               }
               else{
                  rl.setTipo("T_CARACTERE");

                  String mem = proximaPosicaoMemoria("T_CARACTERE", 0);
                  codigo.append("byte ?               ; var carac em "+mem+ "\n");
                  rl.setPosicaoMemoria(mem);
               } 
            }
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
            break;
         }

         

         if(token_atual == NumToken.T_ABRECOLCHETE){
            token_esperado = NumToken.T_ABRECOLCHETE;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
         
            tamanhoVetor = al.registroLexico.get(contRegLex-1).getLexema().length();

            if(token_atual == NumToken.T_CONSTANTE && tamanhoVetor > 0 && tamanhoVetor <= 4096){
               token_esperado = NumToken.T_CONSTANTE;
               casaToken(token_esperado);
               contRegLex++;
               token_atual = al.registroLexico.get(contRegLex).getToken();

               //Acao 40
               rl = al.registroLexico.get(contRegLex-1);
               if(!(rl.getTipo().equals("T_INTEIRO"))){
                  System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getLexema() + "]");
                  System.exit(0);
               }

               if(flag == 0){
                  rl.setTipo("T_INTEIRO");

                  String mem = proximaPosicaoMemoria("T_INTEIRO", 0);
                  codigo.append("sword "+ tamanhoVetor + " DUP(?)				; var inteira em "+mem + "\n");
                  rl.setPosicaoMemoria(mem);
               }
               else{
                  rl.setTipo("T_CARACTERE");

                  String mem = proximaPosicaoMemoria("T_CARACTERE", 0);
                  codigo.append("byte "+ tamanhoVetor + " DUP(?)			; var carac em "+mem+ "\n");
                  rl.setPosicaoMemoria(mem);
               } 

            }
            else{
               System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
               System.exit(0);
               break;
            }
         
            if(token_atual == NumToken.T_FECHACOLCHETE){
               token_esperado = NumToken.T_FECHACOLCHETE;
               casaToken(token_esperado);
               contRegLex++;
               token_atual = al.registroLexico.get(contRegLex).getToken();

               if(token_atual == NumToken.T_IGUAL){
                  System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
                  System.exit(0);
                  break;
               }
            }
         }
         
         if(token_atual == NumToken.T_IGUAL){
            token_esperado = NumToken.T_IGUAL;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            valor();
         }

         while(token_atual == NumToken.T_VIRGULA){
            token_esperado = NumToken.T_VIRGULA;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();	
         
            if(token_atual == NumToken.T_ID){
               token_esperado = NumToken.T_ID;
               casaToken(token_esperado);
               contRegLex++;
               token_atual = al.registroLexico.get(contRegLex).getToken();

               rl = al.ts.pesquisa(al.registroLexico.get(contRegLex-1).getLexema());
               //Acao 41
               if(rl.getClasse() != null){
                  System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":classe de identificador incompativel [" +  al.registroLexico.get(contRegLex-1).getLexema() + "]");
                  System.exit(0);
               }
               else{
                  rl.setClasse("C_VARIAVEL");
                  if(flag == 0){
                     rl.setTipo("T_INTEIRO");
                  }
                  else{
                     rl.setTipo("T_CARACTERE");
                  } 
               }
            }
            else{
               System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
               System.exit(0);
               break;
            }
            

            if(token_atual == NumToken.T_ABRECOLCHETE){
               token_esperado = NumToken.T_ABRECOLCHETE;
               casaToken(token_esperado);
               contRegLex++;
               token_atual = al.registroLexico.get(contRegLex).getToken();
            
               if(token_atual == NumToken.T_CONSTANTE && Integer.parseInt(al.registroLexico.get(contRegLex).getLexema()) > 0){
                  token_esperado = NumToken.T_CONSTANTE;
                  casaToken(token_esperado);
                  contRegLex++;
                  token_atual = al.registroLexico.get(contRegLex).getToken();

                  //Acao 42
                  rl = al.ts.pesquisa(al.registroLexico.get(contRegLex-1).getLexema());
                  if(!(rl.getTipo().equals("T_INTEIRO"))){
                     System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getLexema() + "]");
                     System.exit(0);
                  }
               }
               else{
                  System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
                  System.exit(0);
                  break;
               }
            
               if(token_atual == NumToken.T_FECHACOLCHETE){
                  token_esperado = NumToken.T_FECHACOLCHETE;
                  casaToken(token_esperado);
                  contRegLex++;
                  token_atual = al.registroLexico.get(contRegLex).getToken();

                  if(token_atual == NumToken.T_IGUAL){
                     System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
                     System.exit(0);
                     break;
                  }
               }
            }
            if(token_atual == NumToken.T_IGUAL){
            token_esperado = NumToken.T_IGUAL;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            
            //Acao 43
            String tipo_valor = valor();
            if(!rl.getTipo().equals(tipo_valor)){
               System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getLexema() + "]");
               System.exit(0);
            }
         }
         
      }

         
         if(token_atual == NumToken.T_PONTOVIRGULA){
            token_esperado = NumToken.T_PONTOVIRGULA;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken(); 
         }
      }while(token_atual == NumToken.T_INTEGER || token_atual == NumToken.T_CHAR);
   }

   //DEC_CONST_ESCALAR: const id = VALOR;
   public static void decConstEscalar(){
      //POG
      RegistroLexico rl = al.ts.pesquisa(al.registroLexico.get(contRegLex-1).getLexema());

      if(token_atual == NumToken.T_CONST){
         token_esperado = NumToken.T_CONST;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
         System.exit(0);
      }
   
      if(token_atual == NumToken.T_ID){
         token_esperado = NumToken.T_ID;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();

         //Acao 35
         rl = al.ts.pesquisa(al.registroLexico.get(contRegLex-1).getLexema());
         if(rl.getClasse() == null){
            rl.setClasse("C_CONSTANTE");
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":identificador ja declarado [" +  al.registroLexico.get(contRegLex-1).getLexema() + "]");
            System.exit(0);
         }
      }
      
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }
   
      if(token_atual == NumToken.T_IGUAL){
         token_esperado = NumToken.T_IGUAL;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
         System.exit(0);
      }
      String valor_tipo = valor();

      rl.setTipo(valor_tipo);
      cmdNulo();
   }

   //COMANDOS: CMD_ATRIB | CMD_REPET | CMD_TEST | CMD_NULO | CMD_LEIT | CMD_ESCR
   public static void comandos(){
      if(token_atual == NumToken.T_ID)	{	cmdAtrib();	}
      else if(token_atual == NumToken.T_FOR)	{	cmdRepet();	}
      else if(token_atual == NumToken.T_IF)	{	cmdTest();	}
      else if(token_atual == NumToken.T_PONTOVIRGULA)	{	cmdNulo();	}
      else if(token_atual == NumToken.T_READLN)	{	cmdLeit();	}
      else if(token_atual == NumToken.T_WRITELN || token_atual == NumToken.T_WRITE)	{	cmdEscr();	}
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
         System.exit(0);
      }
   }

   //CMD_ATRIB: id = EXP;
   public static void cmdAtrib(){
      RegistroLexico rl = al.ts.pesquisa(al.registroLexico.get(contRegLex-1).getLexema());
      if(token_atual == NumToken.T_ID){
         token_esperado = NumToken.T_ID;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();

         rl = al.ts.pesquisa(al.registroLexico.get(contRegLex-1).getLexema());
         //Acao 33
         if(rl.getClasse() == null || rl.getClasse().equals("C_CONSTANTE")){
            System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":classe de identificador incompativel [" +  al.registroLexico.get(contRegLex-1).getLexema() + "]");
            System.exit(0);
         }
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }
   
      if(token_atual == NumToken.T_IGUAL){
         token_esperado = NumToken.T_IGUAL;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }
   
      String exp_tipo = exp();

      if(!rl.getTipo().equals(exp_tipo)){
         System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":tipo incompativel [" +  al.registroLexico.get(contRegLex-1).getLexema() + "]");
         System.exit(0);
      }
      cmdNulo();
   }

   //CMD_REPET: for id = EXP to EXP [step constante] do ('{'{COMANDOS}*'}' | COMANDOS)
   public static void cmdRepet(){
      String cmdRepet_tipo = "";

      if(token_atual == NumToken.T_FOR){
         token_esperado = NumToken.T_FOR;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }
   
      if(token_atual == NumToken.T_ID){
         token_esperado = NumToken.T_ID;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }
      if(token_atual == NumToken.T_IGUAL){
         token_esperado = NumToken.T_IGUAL;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
         System.exit(0);
      }
   
      cmdRepet_tipo = exp();

      //Acao 30
      if(!cmdRepet_tipo.equals("T_INTEIRO")){
         System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":tipo de identificador incompativel [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
         System.exit(0);
      }
   
      if(token_atual == NumToken.T_TO){
         token_esperado = NumToken.T_TO;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
         System.exit(0);
      }
   
      String cmdRepet1_tipo = exp();

      //Acao 31
      if(!cmdRepet1_tipo.equals("T_INTEIRO")){
         System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":tipo de identificador incompativel [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
         System.exit(0);
      }

      if(token_atual == NumToken.T_STEP){
         token_esperado = NumToken.T_STEP;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      	
         if(token_atual == NumToken.T_CONSTANTE){
            token_esperado = NumToken.T_CONSTANTE;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();

            //Acao 32
            if(!al.registroLexico.get(contRegLex-1).getTipo().equals("T_INTEIRO")){
               System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":tipo de identificador incompativel [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
               System.exit(0);
            }
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
         }
      }
   
      	//tratar o caso de nao ter o step e o valor ser 1
   
      if(token_atual == NumToken.T_DO){
         token_esperado = NumToken.T_DO;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }
   
      if(token_atual == NumToken.T_ABRECHAVE){
         token_esperado = NumToken.T_ABRECHAVE;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
         while(token_atual == NumToken.T_ID || token_atual == NumToken.T_FOR || token_atual == NumToken.T_IF || token_atual == NumToken.T_WRITELN ||
            token_atual == NumToken.T_PONTOVIRGULA || token_atual == NumToken.T_READLN || token_atual == NumToken.T_WRITE
            ){
               comandos();
            }
         if(token_atual == NumToken.T_FECHACHAVE){
            
            token_esperado = NumToken.T_FECHACHAVE;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
         }
      }
      else{
         comandos();   
      }
   }

   //CMD_TEST: if EXP then ('{'{COMANDOS}*'}' | COMANDOS) [else ('{'{COMANDOS}*'}' | COMANDOS)]
   public static void cmdTest(){
      if(token_atual == NumToken.T_IF){
         token_esperado = NumToken.T_IF;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }

      //Acao 29
      String exp_tipo = exp();
      if(!exp_tipo.equals("T_LOGICO")){
         System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":tipo de identificador incompativel [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
         System.exit(0);
      }

      if(token_atual == NumToken.T_THEN){
         token_esperado = NumToken.T_THEN;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }
   
      if(token_atual == NumToken.T_ABRECHAVE){
         token_esperado = NumToken.T_ABRECHAVE;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
         while(token_atual == NumToken.T_ID || token_atual == NumToken.T_FOR || token_atual == NumToken.T_IF || token_atual == NumToken.T_WRITELN ||
            token_atual == NumToken.T_PONTOVIRGULA || token_atual == NumToken.T_READLN || token_atual == NumToken.T_WRITE
            ){
            comandos();
         }
         
         if(token_atual == NumToken.T_FECHACHAVE){
            token_esperado = NumToken.T_FECHACHAVE;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
         }
      }
      else{
         comandos();
      }
      
   
      if(token_atual == NumToken.T_ELSE){
         token_esperado = NumToken.T_ELSE;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
         if(token_atual == NumToken.T_ABRECHAVE){
            token_esperado = NumToken.T_ABRECHAVE;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            while(token_atual == NumToken.T_ID || token_atual == NumToken.T_FOR || token_atual == NumToken.T_IF || token_atual == NumToken.T_WRITELN ||
            token_atual == NumToken.T_PONTOVIRGULA || token_atual == NumToken.T_READLN || token_atual == NumToken.T_WRITE
            ){
               comandos();
            }
            if(token_atual == NumToken.T_FECHACHAVE){
               token_esperado = NumToken.T_FECHACHAVE;
               casaToken(token_esperado);
               contRegLex++;
               token_atual = al.registroLexico.get(contRegLex).getToken();
            }
            else{
               System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
               System.exit(0);
            }
         }
         else{
            comandos();
         }
      }
   }

   //CMD_NULO: ;
   public static void cmdNulo(){
      if(token_atual == NumToken.T_PONTOVIRGULA){
         token_esperado = NumToken.T_PONTOVIRGULA;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
         System.exit(0);
      }
   }

   //CMD_LEIT: readln '('(constante| hexa | id)')';
   public static void cmdLeit(){
      String fator_tipo = "";
      if(token_atual == NumToken.T_READLN){
         token_esperado = NumToken.T_READLN;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }
   
      if(token_atual == NumToken.T_ABREPAR){
         token_esperado = NumToken.T_ABREPAR;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }
   
      if(token_atual == NumToken.T_ID){
         token_esperado = NumToken.T_ID;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();

         RegistroLexico rl = al.ts.pesquisa(al.registroLexico.get(contRegLex-1).getLexema());
         //Acao 28
         if(rl.getClasse() == null && (!rl.getClasse().equals("C_VARIAVEL") && (!rl.getTipo().equals("T_CARACTERE")))){
            System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":identificador nao declarado [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
            System.exit(0);
         }
         else{
            fator_tipo = rl.getTipo();
         }
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
         System.exit(0);
      }
      
      if(token_atual == NumToken.T_FECHAPAR){
         token_esperado = NumToken.T_FECHAPAR;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
         System.exit(0);
      }
      cmdNulo();
   }

   //CMD_ESCR: (write|writeln) '(' EXP {,EXP}* ')';
   public static void cmdEscr(){
      if(token_atual == NumToken.T_WRITE || token_atual == NumToken.T_WRITELN){
         if(token_atual == NumToken.T_WRITE){
            token_esperado = NumToken.T_WRITE;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
         }
         else{
            token_esperado = NumToken.T_WRITELN;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
         }
         if(token_atual == NumToken.T_ABREPAR){
            token_esperado = NumToken.T_ABREPAR;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
         }
         exp();
         if(token_atual == NumToken.T_VIRGULA){
            while(token_atual == NumToken.T_VIRGULA){
               token_esperado = NumToken.T_VIRGULA;
               casaToken(token_esperado);
               contRegLex++;
               token_atual = al.registroLexico.get(contRegLex).getToken();
               exp();
            }
         }
         if(token_atual == NumToken.T_FECHAPAR){
            token_esperado = NumToken.T_FECHAPAR;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
         }
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      } 
      cmdNulo();
   }

   //EXP: EXPS [(=|<>|<|>|<=|>=) EXPS]
   public static String exp(){
      //Acao 20
      String exp_tipo = exps();
      int flag_exp = -1;

      if(token_atual == NumToken.T_IGUAL || token_atual == NumToken.T_DIFERENTE || token_atual == NumToken.T_MENOR ||
      	token_atual == NumToken.T_MAIOR || token_atual == NumToken.T_MENORIGUAL || token_atual == NumToken.T_MAIORIGUAL){
      
         if(token_atual == NumToken.T_IGUAL){
            token_esperado = NumToken.T_IGUAL;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 21
            flag_exp = 0;
         }
         else if(token_atual == NumToken.T_DIFERENTE){
            token_esperado = NumToken.T_DIFERENTE;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 22
            flag_exp = 1;
         }
         else if(token_atual == NumToken.T_MENOR){
            token_esperado = NumToken.T_MENOR;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 23
            flag_exp = 2;
         }
         else if(token_atual == NumToken.T_MAIOR){
            token_esperado = NumToken.T_MAIOR;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 24
            flag_exp = 3;
         }
         else if(token_atual == NumToken.T_MENORIGUAL){
            token_esperado = NumToken.T_MENORIGUAL;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 25
            flag_exp = 4;
         }
         else{
            token_esperado = NumToken.T_MAIORIGUAL;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 26
            flag_exp = 5;
         }
         
         //Acao 27
         String exps_tipo = exps();

         if(exp_tipo.equals(exps_tipo)){
            if(exps_tipo.equals("T_CARACTERE") && (flag_exp == 0 || flag_exp == 1)){
               exp_tipo = "T_LOGICO";
            }
            else if(exps_tipo == "T_INTEIRO"){
               exp_tipo = "T_LOGICO";
            }
            else{
               System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":Tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
               System.exit(0);
            }
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":Tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
            System.exit(0);
         }
      }   
      return exp_tipo;
   }

   //EXPS: [+|-] TERMO {(+|-|or) TERMO}*
   public static String exps(){
      String exps_tipo = "";
      int flag_exps = -1;

      if(token_atual == NumToken.T_MAIS || token_atual == NumToken.T_MENOS){
         if(token_atual == NumToken.T_MENOS){
            token_esperado = NumToken.T_MENOS;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
         }
         else{
            token_esperado = NumToken.T_MAIS;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
         }
      }
      //Acao 15
      exps_tipo = termo();
   
      while(token_atual == NumToken.T_MAIS || token_atual == NumToken.T_MENOS || token_atual == NumToken.T_OR){
         if(token_atual == NumToken.T_MENOS){
            token_esperado = NumToken.T_MENOS;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 17
            flag_exps = 1;
         }
         else if(token_atual == NumToken.T_MAIS){
            token_esperado = NumToken.T_MAIS;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 16
            flag_exps = 0;
         }
         else{
            token_esperado = NumToken.T_OR;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 18
            flag_exps = 2;
         }

         //Acao 19
         String termo1_tipo = termo();

         if(termo1_tipo.equals(exps_tipo)){
            if((flag_exps == 0 || flag_exps == 1) && exps_tipo.equals("T_INTEIRO")){
               exps_tipo = "T_INTEIRO";
            }
            else if(flag_exps == 2 && exps_tipo.equals("T_LOGICO")){
               exps_tipo = "T_LOGICO";
            }
            else{
               System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":Tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
               System.exit(0);
            }
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":Tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
            System.exit(0);
         }




      }
      return exps_tipo;
   }

   //TERMO: FATOR {(*|/|and|%) FATOR}*
   public static String termo(){
      //Acao 9
      String termo_tipo = fator();
      int flag_termo = -1;

      while(token_atual == NumToken.T_ASTERISCO || token_atual == NumToken.T_BARRA || token_atual == NumToken.T_AND || token_atual == NumToken.T_PORCENTO){
         if(token_atual == NumToken.T_ASTERISCO){
            token_esperado = NumToken.T_ASTERISCO;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 10
            flag_termo = 0;
         }
         else if(token_atual == NumToken.T_BARRA){
            token_esperado = NumToken.T_BARRA;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 11
            flag_termo = 1;
         }
         else if(token_atual == NumToken.T_AND){
            token_esperado = NumToken.T_AND;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 12
            flag_termo = 2;
         }
         else{
            token_esperado = NumToken.T_PORCENTO;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 13
            flag_termo = 3;
         }
         //Acao 14
         String fator1_tipo = fator();

         if(fator1_tipo.equals(termo_tipo)){
            if(flag_termo == 0 || flag_termo == 1 || flag_termo == 3 && termo_tipo.equals("T_INTEIRO")){
               termo_tipo = "T_INTEIRO";
            }
            else if (flag_termo == 2 && termo_tipo.equals("T_LOGICO")){
               termo_tipo = "T_LOGICO";
            }
            else{
               System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":Tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
               System.exit(0);
            }
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":Tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
            System.exit(0);
         }

      }
      return termo_tipo;
   }

   //FATOR: id['[' EXP ']'] | VALOR | !FATOR | '('EXP')'
   public static String fator(){
      String fator_tipo = ""; 
      Retorno fator_retorno = new Retorno();
      if(token_atual == NumToken.T_ID){
         token_esperado = NumToken.T_ID;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();

         RegistroLexico rl = al.ts.pesquisa(al.registroLexico.get(contRegLex-1).getLexema());
         //Acao 3
         if(rl.getClasse() == null){
            System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":identificador nao declarado [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
            System.exit(0);
         }
         else{
            fator_tipo = rl.getTipo();
         }

         if(token_atual == NumToken.T_ABRECOLCHETE){
            token_esperado = NumToken.T_ABRECOLCHETE;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            
            //Acao 4
            fator_tipo = exp();
            if(!fator_tipo.equals("T_INTEIRO")){
               System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":Tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
               System.exit(0);
            }

            if(token_atual == NumToken.T_FECHACOLCHETE){
               token_esperado = NumToken.T_FECHACOLCHETE;
               casaToken(token_esperado);
               contRegLex++;
               token_atual = al.registroLexico.get(contRegLex).getToken();
            }
            else{
               System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
               System.exit(0);
            }
         }
      }
      else if(token_atual == NumToken.T_CONSTANTE || token_atual == NumToken.T_HEXA || token_atual == NumToken.T_STRING){
         fator_tipo = valor();
         codigo.append("dseg SEGMENT PUBLIC");
         codigo.append("byte "+al.registroLexico.get(contRegLex).getLexema()+"$");
         codigo.append("dseg ENDS");
         fator_retorno.setEnd(proximaPosicaoMemoria("T_CARACTERE", 0));
      }
      
      else if(token_atual == NumToken.T_NOT){
         token_esperado = NumToken.T_NOT;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
         //Acao 2
         if(al.registroLexico.equals("T_LOGICO")){
               fator_tipo = fator();

               fator_end = novoTemporario(fator_tipo);
               codigo.append("mov AX, "+fator_end);
               codigo.append("neg AX");
               codigo.append("add AX, 1");
               codigo.append("mov " + fator_end+", AX");

         }else{
            System.out.println(al.registroLexico.get(contRegLex-1).getLinha() + ":Tipos incompativeis [" +  al.registroLexico.get(contRegLex-1).getToken() + "]");
            System.exit(0);
         }
      }
      
      else if(token_atual == NumToken.T_ABREPAR){
         token_esperado = NumToken.T_ABREPAR;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
         //Acao 1
         fator_tipo = exp();
         if(token_atual == NumToken.T_FECHAPAR){
            token_esperado = NumToken.T_FECHAPAR;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
         }
         else{
            System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
         }
      }
   
      
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
         System.exit(0);
      }
      return fator_tipo;
   }

   //VALOR: [+|-]constante | hexa | string | caractere
   public static String valor(){
      String valor_tipo = "";
      if(token_atual == NumToken.T_MAIS || token_atual == NumToken.T_MENOS || token_atual == NumToken.T_CONSTANTE){
         if(token_atual == NumToken.T_MAIS){
            token_esperado = NumToken.T_MAIS;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            if(token_atual == NumToken.T_CONSTANTE){
               token_esperado = NumToken.T_CONSTANTE;
               casaToken(token_esperado);
               contRegLex++;
               token_atual = al.registroLexico.get(contRegLex).getToken();
               //Acao 5
               valor_tipo = al.registroLexico.get(contRegLex-1).getTipo();
            }
         }
         else if(token_atual == NumToken.T_MENOS){
            token_esperado = NumToken.T_MENOS;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            if(token_atual == NumToken.T_CONSTANTE){
               token_esperado = NumToken.T_CONSTANTE;
               casaToken(token_esperado);
               contRegLex++;
               token_atual = al.registroLexico.get(contRegLex).getToken();
               //Acao 5
               valor_tipo = al.registroLexico.get(contRegLex-1).getTipo();
            }
         }
         else{
            token_esperado = NumToken.T_CONSTANTE;
            casaToken(token_esperado);
            contRegLex++;
            token_atual = al.registroLexico.get(contRegLex).getToken();
            //Acao 5
            valor_tipo = al.registroLexico.get(contRegLex-1).getTipo();
         }
      }
      else if(token_atual == NumToken.T_HEXA){
         token_esperado = NumToken.T_HEXA;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
         //Acao 6
         valor_tipo = al.registroLexico.get(contRegLex-1).getTipo();
      }
      else if(token_atual == NumToken.T_STRING){
         token_esperado = NumToken.T_STRING;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
         //Acao 7
         valor_tipo = al.registroLexico.get(contRegLex-1).getTipo();
      }
      else if(token_atual == NumToken.T_CARACTERE){
         token_esperado = NumToken.T_CARACTERE;
         casaToken(token_esperado);
         contRegLex++;
         token_atual = al.registroLexico.get(contRegLex).getToken();
         //Acao 8
         valor_tipo = al.registroLexico.get(contRegLex-1).getTipo();
      }
      else{
         System.out.println(al.registroLexico.get(contRegLex).getLinha() + ":token nao esperado [" +  al.registroLexico.get(contRegLex).getToken() + "]");
            System.exit(0);
      }

      return valor_tipo;
   }
   
   public static String proximaPosicaoMemoria( String tipo, int tamanho ){
      String resp = memoria+"h";
      if(tipo.equals("T_CARACTERE") && tamanho == 0){
            memoria = memoria + 1;
      }else if(tipo.equals("T_INTEIRO") && tamanho == 0){
            memoria = memoria + 2;
      }else if(tipo.equals("T_CARACTERE") && tamanho > 0){
            memoria = memoria + tamanho;
      }else if(tipo.equals("T_INTEIRO") && tamanho > 0){
            memoria = memoria + tamanho*2;
      }
      return resp;
}

public static String novoTemporario( String tipo ){
      String resp = "DS:"+temporarios;
      if(tipo.equals("T_INTEIRO")){
            temporarios = temporarios + 2;
      }else if(tipo.equals("T_CARACTERE")){
            temporarios = temporarios + 1;
      }
      
      return resp;
}

}