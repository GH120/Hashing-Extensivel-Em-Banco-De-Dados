public class Registro {
    public int linha;
    public int ano;

    public Registro(int linha, int ano) {
        this.linha = linha;
        this.ano = ano;
    }

    public String toString(){
        return "Linha: " + linha + ", " + "Valor: " + ano;
    }
}