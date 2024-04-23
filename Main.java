class Main {
    
    public static void main(String[] args){
        
        // Bucket teste = new Bucket(29);

        // teste.adicionarRegistro(1,2002);
        // teste.adicionarRegistro(3,2022);
        // teste.adicionarRegistro(0,2012);

        // teste.salvarBucket();
        // teste.salvarBucket();
        // teste.salvarBucket();

        Diretorio teste = new Diretorio(4);

        teste.inserirBucket(new Bucket(20));

        try{
            teste.armazenarDiretorio("diretorio.txt");

        }
        catch(Exception e){

        }


    }
}