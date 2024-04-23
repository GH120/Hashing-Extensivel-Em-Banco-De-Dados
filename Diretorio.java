    import java.util.ArrayList;
    import java.io.*;
    import java.util.*;
    

    //Representa o diretório que aponta para buckets
    //O diretório vai guardar o número dos arquivos dos buckets
    //O diretório vai guardar uma lista de ponteiros para esses numeros
    //O diretório vai ter um método que retorna UM e apenas UM bucket a partir do número guardado na posição i da lista
    
    public class Diretorio {

        public  int profundidade;
        private ArrayList<Integer> ponteiros; // Lista de ponteiros para os números dos arquivos dos buckets
        private ArrayList<Integer> profundidadesLocais;

        public Diretorio(int depth) {

            profundidade        = depth;
            ponteiros           = this.criarPonteiros(depth);

            //Preenche um array de profundidades locais para cada ponteiro
            profundidadesLocais = new ArrayList<>(Collections.nCopies(ponteiros.size(), depth));

        }
        
        public void mudarPonteiro(int idBucket, int indice){
            this.ponteiros.set(indice, idBucket);
        }
    
        // Método para obter um bucket a partir do número guardado na posição 'i' da lista
        public Bucket obterBucket(int indice) {
            
            int numeroBucket = ponteiros.get(indice);

            return new Bucket(numeroBucket).carregarBucket();
        }

        public void duplicarDiretorio() {

            int tamanhoAtual = ponteiros.size();
            int novoTamanho = tamanhoAtual * 2;
            
            // Duplica o array de ponteiros
            ArrayList<Integer> novoDiretorio = new ArrayList<>(novoTamanho);
            novoDiretorio.addAll(ponteiros);
            novoDiretorio.addAll(ponteiros); // Clona todas as posições iniciais
            
            // Atualiza o diretório com o novo array de ponteiros
            ponteiros = novoDiretorio;

            profundidade += 1;
        }

        public void armazenarDiretorio(String nomeArquivo) throws IOException {
            try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
                writer.println("Profundidade global: " + profundidade);
                writer.println("Buckets:");
                for (int i = 0; i < ponteiros.size(); i++) {
                    writer.println((i + 1) + " - " + ponteiros.get(i));
                }
            }
        }
    
        
        public void carregarDiretorio(String nomeArquivo) throws IOException {
            ponteiros.clear(); 
    
            try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Profundidade global:")) {
                        profundidade = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
                    } else if (line.startsWith("Buckets:")) {
                        // Ignora esta linha
                    } else {
                        // Extrai o número do ponteiro e adiciona à lista de ponteiros
                        int numeroBucket = Integer.parseInt(line.substring(line.indexOf("-") + 1).trim());
                        ponteiros.add(numeroBucket);
                    }
                }
            }
        }

        // Método para imprimir todos os ponteiros do diretório
        public void imprimirPonteiros() {
            System.out.println("Ponteiros do Diretório:");
            for (int i = 0; i < ponteiros.size(); i++) {
                System.out.println("Posição " + i + ": " + Integer.toBinaryString(ponteiros.get(i)));
            }
        }

        public Integer getProfundidade(Bucket Bucket){

            return profundidadesLocais.get(Bucket.numero);
        }

        private ArrayList<Integer> criarPonteiros(int profundidade){

            var lista  = new ArrayList<Integer>();

            double quantidade = Math.pow(2, profundidade);

            for (int i = 0; i < quantidade; i++) {
                lista.add(i);
            }

            return lista;
        }
    
    }
    
