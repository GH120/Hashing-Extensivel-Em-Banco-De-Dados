public class Main {

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
