public class Main {

    //Ligar csv aos registros
    //Ligar operações ao csv
    //Mudar nome dos arquivos para adicionar 0 a esquerda
    //Colocar while no duplicar bucket, 2005 e 3*2013 -> Feito por naturalidade
    //Tornar bucket singleton
    public static void main(String[] args) {

        // // Verifica se foi fornecido o arquivo de entrada como argumento
        // if (args.length < 1) {
        //     System.out.println("Uso: java Main <arquivo_entrada>");
        //     return;
        // }

        var processador = new ProcessadorComandos();

        processador.processarArquivo("arquivos/in.txt", "arquivos/out.txt");
    }
}
