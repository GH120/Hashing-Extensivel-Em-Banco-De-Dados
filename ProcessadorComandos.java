import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProcessadorComandos {
    

    public void processarArquivo(String arquivoEntrada, String arquivoSaida){

        try(BufferedReader reader = new BufferedReader(new FileReader(arquivoEntrada))){
            // Leitura do arquivo de entrada
            

            // Leitura da profundidade global
            String primeiraLinha = reader.readLine();
            int profundidadeGlobal = extrairProfundidadeGlobal(primeiraLinha);

            // Criação do objeto HashingExtensivel com a profundidade global fornecida
            HashingExtensivel hashingExtensivel = new HashingExtensivel(profundidadeGlobal);

            // Criação do arquivo de saída
            BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoSaida));
            writer.write(primeiraLinha); // Escreve a profundidade global no arquivo de saída
            writer.newLine();

            // Processamento das operações do arquivo de entrada
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(":");
                String operacao = partes[0];
                int valor = Integer.parseInt(partes[1]);

                // Executa a operação correspondente no HashingExtensivel
                switch (operacao) {
                    case "INC":
                        hashingExtensivel.inserirValor(valor, valor); 
                        writer.write("INC:" + valor + "/" + hashingExtensivel.profundidadeGlobal + "," + hashingExtensivel.profundidadeLocal(valor));
                        writer.newLine();
                        break;
                    case "REM":
                        int valoresDeletados  = hashingExtensivel.deletarValor(valor);
                        writer.write("REM:" + valor + "/" + valoresDeletados + "," + hashingExtensivel.profundidadeGlobal + "," + hashingExtensivel.profundidadeLocal(valor));
                        writer.newLine();
                        break;
                    case "BUS":
                        int quantidadeSelecionada = hashingExtensivel.buscarValor(valor).size();
                        writer.write("BUS:" + valor + "/" + quantidadeSelecionada);
                        writer.newLine();
                        break;
                    default:
                        System.out.println("Operação inválida: " + operacao);
                }
            }

            //Salva o diretorio final para a analise
            hashingExtensivel.diretorio.armazenarDiretorio("arquivos/diretorio");

            // Escreve a última linha no arquivo de saída
            writer.write("P:/" + profundidadeGlobal);
            writer.newLine();

            // Fechamento dos recursos
            reader.close();
            writer.close();

            System.out.println("Operações concluídas. Resultados escritos em " + arquivoSaida);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Extrai a profundidade global do formato PG/<profundidade_global>
    private static int extrairProfundidadeGlobal(String primeiraLinha) {
        String[] partes = primeiraLinha.split("/");
        return Integer.parseInt(partes[1]);
    }
}

