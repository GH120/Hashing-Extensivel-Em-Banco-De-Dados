public class Main {

    //Mudar nome dos arquivos para adicionar 0 a esquerda
    //Colocar while no duplicar bucket, 2005 e 3*2013 -> Feito por naturalidade
    //Tornar bucket singleton
    //Lidar com caso de divisão do diretório: output errado no ultimo remover

    //93% completo
    public static void main(String[] args) {

        // // Verifica se foi fornecido o arquivo de entrada como argumento
        // if (args.length < 1) {
        //     System.out.println("Uso: java Main <arquivo_entrada>");
        //     return;
        // }

        var processador = new ProcessadorComandos(
                                                "arquivos/in.txt", 
                                                "arquivos/out.txt",
                                                "arquivos/compras.csv" 
                                                );

        processador.processarArquivo();
    }
}
