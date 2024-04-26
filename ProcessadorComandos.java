import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessadorComandos {

    String arquivoEntrada;
    String arquivoSaida;
    String arquivoCSV;

    HashingExtensivel hashingExtensivel;

    ProcessadorComandos(String arquivoEntrada, String arquivoSaida, String arquivoCSV){
        this.arquivoEntrada = arquivoEntrada;
        this.arquivoSaida   = arquivoSaida  ;
        this.arquivoCSV     = arquivoCSV    ;
    }
    

    public void processarArquivo(){

        try(BufferedReader reader = new BufferedReader(new FileReader(arquivoEntrada))){

            // Leitura da profundidade global
            String primeiraLinha = reader.readLine();
            int profundidadeGlobal = extrairProfundidadeGlobal(primeiraLinha);

            // Criação do objeto HashingExtensivel com a profundidade global fornecida
            hashingExtensivel = new HashingExtensivel(profundidadeGlobal);

            // Criação do arquivo de saída
            BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoSaida));
            writer.write(primeiraLinha); // Escreve a profundidade global no arquivo de saída
            writer.newLine();

            //Autoexplicatório
            LerArquivoTratarCasosEscreverNoOutput(reader, writer);
            
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

    private void LerArquivoTratarCasosEscreverNoOutput(BufferedReader reader, BufferedWriter writer){

        try{
            // Processamento das operações do arquivo de entrada
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(":");
                String operacao = partes[0];
                int valor = Integer.parseInt(partes[1]);

                // Executa a operação correspondente no HashingExtensivel
                switch (operacao) {
                    case "INC":

                        //Procura todos os registros com esse ano no csv
                        for(Registro registro : procurarNoCSV(valor)){
                            hashingExtensivel.inserirValor(registro.linha, registro.valor); 
                        }
                        
                        writer.write("INC:" + valor + "/" + hashingExtensivel.profundidadeGlobal + "," + hashingExtensivel.profundidadeLocal(valor));
                        writer.newLine();
                        break;
                    case "REM":

                        int valoresDeletados = hashingExtensivel.deletarValor(valor); 

                        writer.write("REM:" + valor + "/" + valoresDeletados + "," + hashingExtensivel.profundidadeGlobal + "," + hashingExtensivel.profundidadeLocal(valor));
                        writer.newLine();
                        break;
                    case "BUS=":

                        int quantidadeSelecionada = hashingExtensivel.buscarValor(valor).size();

                        writer.write("BUS:" + valor + "/" + quantidadeSelecionada);
                        writer.newLine();
                        break;
                    default:
                        System.out.println("Operação inválida: " + operacao);
                }
            }
        }

        catch(Exception e){e.printStackTrace();}
        

    }

    private List<Registro> procurarNoCSV(int ano) {
        
        List<Registro> registrosProcurados = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoCSV))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(","); 

                int RegistroAno   =  Integer.parseInt(campos[2]);
                int RegistroLinha =  Integer.parseInt(campos[0]);

                //Se o registro lido na tabela csv tem o ano procurado, adiciona ele a
                if(RegistroAno == ano){

                    Registro registro = new Registro(RegistroLinha,RegistroAno);

                    registrosProcurados.add(registro);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return registrosProcurados;
    }
}

