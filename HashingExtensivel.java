//Implementação da maioria do trabalho
//Aceita incluir registros, deletar registros e buscar registros por igualdade
//Ao inserir um ano, vai ver seu bucket pela função hash
//Construtor aceita a profundidade global, quantidade de armazenamento no bucket e folder do diretório
//Constroi inicialmente 2² buckets vazios

import java.util.*;
import java.util.stream.Collectors;

public class HashingExtensivel {
    
    int   profundidadeGlobal;
    Diretorio diretorio;
    int   BUCKETSIZE = 3;
    

    HashingExtensivel(int profundidadeGlobal){

        this.profundidadeGlobal = profundidadeGlobal;

        this.CriarDiretorio("arquivos/diretorio");

    }

    public void inserir(Registro novoRegistro){

        var bucket = diretorio.obterBucket(getIndice(novoRegistro.ano));

        bucket.carregarBucket();

        boolean cheio = bucket.getRegistros().size() >= BUCKETSIZE;

        if(cheio) 
            handleOverflow(bucket, novoRegistro);
        else {
            bucket.adicionarRegistro(novoRegistro);
            bucket.salvarBucket();
        }
        
    }

    //**Retorna quantos registros foram deletados a partir desse */
    public int deletar(int ano){

        var bucket = diretorio.obterBucket(getIndice(ano));

        bucket.carregarBucket();

        int tamanhoOriginal = bucket.getRegistros().size();

        bucket.getRegistros().removeIf(registro -> registro.ano == ano);

        bucket.salvarBucket();

        handleReduzirDiretorio(bucket);

        return tamanhoOriginal - bucket.getRegistros().size();
    }

    public List<Registro> buscarIgual(int ano){

        var bucket = diretorio.obterBucket(getIndice(ano));

        bucket.carregarBucket();

        return bucket.getRegistros()
                     .stream()
                     .filter(registro -> registro.ano == ano)
                     .collect(Collectors.toList());
    }

    public int profundidadeLocal(int ano){

        var bucket = diretorio.obterBucket(getIndice(ano));

        return diretorio.getProfundidade(bucket);
    }

    private void CriarDiretorio(String arquivoDiretorio){

        diretorio = new Diretorio(profundidadeGlobal);

        //Se tiver diretorio, carrega ele
        try{
            //diretorio.carregarDiretorio(arquivoDiretorio);

            profundidadeGlobal = diretorio.profundidade;
        }
        catch(Exception e){
            diretorio = new Diretorio(profundidadeGlobal);
            profundidadeGlobal = diretorio.profundidade;
        }
    }

    private void handleOverflow(Bucket bucket, Registro novoRegistro){

        int profundidadeLocal = diretorio.getProfundidade(bucket);

        //Se precisar duplicar o diretório, o faz e incrementa profundidade global
        if(profundidadeLocal == profundidadeGlobal){
            diretorio.duplicarDiretorio();
            profundidadeGlobal++;
        }
        
        //A imagem de 00 é 100, a imagem de 1001 é 11001 e assim em diante
        Integer bucketImagem  = bucket.numero + (1 << profundidadeLocal);

        //O step entre cada índice que apontava para o bucket original 16
        double step = Math.pow(2, profundidadeLocal);

        //Percorre o diretorio atualizando ponteiros e profundidades desses buckets
        for(int indice = bucket.numero; indice < diretorio.getLength(); indice += step){

            //Vê no primeiro bit relevante se é original ou não imagem
            boolean imagem = ((indice >> profundidadeLocal) & 1) == 1;

            //Agora o ponteiro aponta para o novo bucket ao invés do antigo
            if(imagem) diretorio.mudarPonteiro(indice, bucketImagem);

            diretorio.incrementarProfundidadeLocal(indice);
        }


        //Copio os registros do bucket para depois esvaziá-lo
        var registros = new ArrayList<>(bucket.getRegistros());
        bucket.limparRegistros();
        bucket.salvarBucket();

        //O novo registro a ser inserido além dos que já existem
        registros.add(novoRegistro);

        //Reinsere os registros agora dividindo entre os dois buckets
        for(Registro registro : registros){
            inserir(registro);
        }
    }



    /** Para caso a profundidade global seja maior que todas as locais
     * e seja desnecessário manter ponteiros redundantes agora que esse bucket está vazio**/

    //Verifica se tem dois e somente dois buckets com profundidade global
    //Verifica se esvaziou um dos buckets
    //Caso 1: só dois bucket -> divide diretório e decrementa profundidade bucket original
    //Caso 2: vários buckets -> decrementa profundidade do bucket irmão e dele mesmo.
    private void handleReduzirDiretorio(Bucket bucketIndeterminado){

        boolean vazio = bucketIndeterminado.getRegistros().size() == 0;

        if (!vazio) return;

        int profundidadeLocal = diretorio.getProfundidade(bucketIndeterminado);

        if(profundidadeLocal == 2) return;

        //Não sabemos se esse bucket é o original ou a imagem dele
        int ponteiroIndeterminado = bucketIndeterminado.numero;

        int     ultimoBit  = (1 << (profundidadeLocal-1));
        boolean ehImagem   = (ponteiroIndeterminado >> (profundidadeLocal - 1)) == 1;

        //A partir do irmão do bucker, conseguimos os ponteiros dos buckets original e imagem.
        int ponteiroIrmao    = (ehImagem)? ponteiroIndeterminado -  ultimoBit : ponteiroIndeterminado + ultimoBit;
        int ponteiroImagem   = (ehImagem)? ponteiroIndeterminado : ponteiroIrmao;       
        int ponteiroOriginal = (ehImagem)? ponteiroIrmao : ponteiroIndeterminado;

        //Não precisamos mais do bucketIndeterminado e ele é vazio, deletamos o arquivo
        bucketIndeterminado.deletarBucket();

        //Carregamos o bucketImagem para caso tenhamos deletado o original
        //Assim, vamos mudar esperar atualizar os ponteiros para adicionar os registros dele no original
        Bucket bucketImagem = diretorio.obterBucket(ponteiroImagem);

        //O step entre cada índice que apontava para o bucket original 
        double step = Math.pow(2, profundidadeLocal-1);

        //Percorre o diretorio atualizando ponteiros e profundidades desses buckets
        for(int indice = ponteiroOriginal; indice < diretorio.getLength(); indice += step){
            //Ponteiro para o bucket imagem agora aponta para o original
            diretorio.mudarPonteiro(indice, ponteiroOriginal);
            diretorio.decrementarProfundidadeLocal(indice);
        }

        //Agora que os ponteiros estão apontando para o bucket original
        //Os registros do bucket imagem, se existirem, são reinseridos no original (que nesse caso foi deletado)
        for(var registro : bucketImagem.getRegistros()){
            inserir(registro);
        }

        //Descarta o bucketImagem
        bucketImagem.deletarBucket(); 
        
        //Lida com o caso de divisão diretório, quando não há nenhuma profundidade local igual a global
        if (profundidadeLocal == profundidadeGlobal){
            
            int maiorProfundidade = Collections.max(diretorio.getProfundidadesLocais());

            if(maiorProfundidade < profundidadeGlobal){
                diretorio.dividirDiretorio();
                profundidadeGlobal --;
            }
        }

        //Agora vamos verificar se os buckets com essa nova profundidade reduzida precisam também sofrer merge
        //Para tal, retiramos o ultimo bit do ponteiro original
        //E com o novo ponteiro resultante pegamos seu irmão (original ou imagem)
        
        profundidadeLocal--;

            ultimoBit         = 1 << (profundidadeLocal-1);
        int mascara           = (ultimoBit << 1) - 1;
        
        int novoPonteiro      = ponteiroOriginal & mascara;
            ehImagem          = (novoPonteiro >> (profundidadeLocal - 1)) == 1;
        int novoPonteiroIrmao = (ehImagem) ? novoPonteiro - ultimoBit : novoPonteiro + ultimoBit;


        //Aplicamos o tratar redução de diretório para ambos os buckets original e imagem dessa profundidade
        handleReduzirDiretorio(diretorio.obterBucket(novoPonteiro));
        handleReduzirDiretorio(diretorio.obterBucket(novoPonteiroIrmao));
    }

    
    /**Função hash a ser definida */
    private Integer Hash(int ano){
        return ano;
    }

    //**Transforma ano em um indice do diretório usando o hash dele e sua máscara */
    private int getIndice(int ano){
        
        int anoHash  = Hash(ano);

        int mascara = (1 << profundidadeGlobal) - 1;

        int indice = anoHash & mascara;

        return indice;
    }

    //Obs: Ao duplicar um bucket, salvar o original com profundidade incrementada
    //Obs: Ao criar uma imagem, lembrar de salvar ela depois
}
