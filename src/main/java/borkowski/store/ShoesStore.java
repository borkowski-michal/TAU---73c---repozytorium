package borkowski.store;
import borkowski.domain.Shoes;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class ShoesStore {
    public static ArrayList<Shoes> shoes = new ArrayList<>();

    public ShoesStore(){}   //konstr. bezparametrowy

    public ArrayList<Shoes> create(Shoes createShoes) throws IllegalArgumentException {   //CREATE
        for (Shoes sh:shoes){   //Sprawdzenie w bazie czy dane ID już w bazie istnieje
            if(createShoes.getId().equals(sh.getId())){
                throw new IllegalArgumentException("In your Database exist domain in this Id");
            }
        }
//        Optional<Shoes> optionalShoe = domain.stream().filter(shoe -> shoe.getId().equals(createShoes.getId())).findFirst();
//        optionalShoe..ifPresentOrElse(domain::add, () -> throw new IllegalArgumentException("In your Database exist domain in this Id"));
        shoes.add(createShoes); //Jesli nie to stwórz
        return shoes;
    }


    public ArrayList<Shoes> readAll(){  //ReadAll
        return shoes;   //Zwroc wszystkie buty
    }


    public Shoes read(Long id){      //READ
        for (Shoes sh: shoes){      //Przejdz po liscie i jak jest to zwroc
            if(id.equals(sh.getId())){
                return sh;
            }
        }
        throw new NoSuchElementException("In your Database doesn't exist domain in this Id");
    }


    public Shoes update( Shoes updateShoes){     //UPDATE
        if(updateShoes.getId().equals(updateShoes.getId())){
            shoes.set(updateShoes.getId().intValue(), updateShoes);
            return updateShoes;
        }
        throw new NoSuchElementException("In your Database doesn't exist domain in this Id");
    }

    public void delete(Long id){
        Shoes shoe = read(id);
        shoes.remove(shoe);
    }
}


