import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class ProcessadorComandos {

    String arquivoEntrada;
    String arquivoSaida;
    String arquivoCSV;

    HashingExtensivel hashingExtensivel;
    BufferedReader    reader;
    BufferedWriter    writer;

    ProcessadorComandos(String arquivoEntrada, String arquivoSaida, String arquivoCSV){
        this.arquivoEntrada = arquivoEntrada;
        this.arquivoSaida   = arquivoSaida  ;
        this.arquivoCSV     = arquivoCSV    ;
    }
    


    public void executarArquivoInput(){

        try{

            //Criação do leitor do arquivo de entrada
            reader = new BufferedReader(new FileReader(arquivoEntrada));

            // Leitura da profundidade global
            String primeiraLinha = reader.readLine();
            int profundidadeGlobal = extrairProfundidadeGlobal(primeiraLinha);

            // Criação do objeto HashingExtensivel com a profundidade global fornecida
            hashingExtensivel = new HashingExtensivel(profundidadeGlobal);

            // Criação do arquivo de saída
            writer = new BufferedWriter(new FileWriter(arquivoSaida));
            writer.write(primeiraLinha); // Escreve a profundidade global no arquivo de saída
            writer.newLine();

            //Lê as operações linha por linha do input e vai realizando no output
            LerArquivoInput();
            
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

    private void LerArquivoInput(){

        try{
            // Processamento das operações do arquivo de entrada
            // Para cada linha extrai a operação e o ano como valor
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(":");
                String operacao = partes[0];
                int valor = Integer.parseInt(partes[1]);

                realizarOperacao(operacao, valor);
            }
        }

        catch(Exception e){e.printStackTrace();}
        

    }

    private void realizarOperacao(String operacao, int ano) throws IOException{

        // Executa a operação correspondente no HashingExtensivel
        switch (operacao) {
            case "INC":

                int profundidadeGlobalAntiga =  hashingExtensivel.profundidadeGlobal;

                //Insere todos os registros com esse ano do csv no hashing
                for(Registro registro : this.procurarRegistrosNoCSV(ano)){
                    hashingExtensivel.inserir(registro); 
                }

                writer.write("INC:" + ano + "/" + hashingExtensivel.profundidadeGlobal + "," + hashingExtensivel.profundidadeLocal(ano));
                writer.newLine();

                if(profundidadeGlobalAntiga != hashingExtensivel.profundidadeGlobal){
                    writer.write("DUP_DIR:" + "/" + hashingExtensivel.profundidadeGlobal + "," + hashingExtensivel.profundidadeLocal(ano));
                    writer.newLine();
                }

                break;
            case "REM":

                int valoresDeletados = hashingExtensivel.deletar(ano); 

                writer.write("REM:" + ano + "/" + valoresDeletados + "," + hashingExtensivel.profundidadeGlobal + "," + hashingExtensivel.profundidadeLocal(ano));
                writer.newLine();
                break;
            case "BUS=":

                int quantidadeSelecionada = hashingExtensivel.buscarIgual(ano).size();

                writer.write("BUS:" + ano + "/" + quantidadeSelecionada);
                writer.newLine();
                break;
            default:
                System.out.println("Operação inválida: " + operacao);
        }
    }

    /** Abre o arquivo csv para extrair os registros da tabela com esse ano e os retorna*/
    private List<Registro> procurarRegistrosNoCSV(int ano) {
        
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

