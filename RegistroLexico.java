/*
* @author Patricia Ferreira Lima
* @author Jorge Mauro Goncalves
* @author Paulo Victor de Oliveira Leal
* Classe responsavel por registrar um lexema a um token
*/

class RegistroLexico{

  public String lexema;
  public  NumToken token;
  private int linha;
  private String tipo;
  private String classe;
  private String posicaoMemoria;
  private int tamanho;
  
    //No AnalisadorSemantico
  public RegistroLexico(String lexema, NumToken token, String classe, String tipo) {
    this.lexema = lexema;
    this.token = token;
    this.classe = classe;
    this.tipo = tipo;
  }
  
  //No Analisador Lexico serve pra todos menos ID
  public RegistroLexico(String lexema, NumToken token, String tipo) {
    this.lexema = lexema;
    this.token = token;
    this.tipo = tipo;
  }
  
  //No Analisador Lexico serve pro ID
  public RegistroLexico(String lexema, NumToken token) {
    this.lexema = lexema;
    this.token = token;
  }
  
  public NumToken getToken(){
      return token;
  }
  public String getLexema(){
      return lexema;
  }

  public void setLinha(int linha){
      this.linha = linha;
  }

  public int getLinha(){
      return linha;
  }

    public String getClasse(){
      return classe;
  }
  public String getTipo(){
      return tipo;
  }
  
  public void setClasse(String classe){
      this.classe = classe;
  }
  
  public void setTipo(String tipo){
      this.tipo = tipo;
  }
  
public String getPosicaoMemoria() {
    return posicaoMemoria;
}

public void setPosicaoMemoria(String posicaoMemoria) {
    this.posicaoMemoria = posicaoMemoria;
}

public int getTamanho() {
    return tamanho;
}

public void setTamanho(int tamanho) {
    this.tamanho = tamanho;
}
}
