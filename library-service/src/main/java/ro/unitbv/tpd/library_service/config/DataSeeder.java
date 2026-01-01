package ro.unitbv.tpd.library_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.unitbv.tpd.library_service.dto.BookRequest;
import ro.unitbv.tpd.library_service.repository.BookRepository;
import ro.unitbv.tpd.library_service.service.BookService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BookService bookService;
    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            System.out.println("🌱 Database is empty. Seeding demo books...");
            getDemoBooks().forEach(book -> {
                try {
                    bookService.saveBook(book);
                    System.out.println("✅ Inserted: " + book.getTitle());
                } catch (Exception e) {
                    System.err.println("❌ Failed to insert: " + book.getTitle() + " -> " + e.getMessage());
                }
            });
        } else {
            System.out.println("📚 Database already contains books. Skipping seed.");
        }
    }

    private List<BookRequest> getDemoBooks() {
        return List.of(
                // 1. Sci-Fi (Bun pentru teste de "spațiu", "planetă", "politică")
                createBook("Dune", "Frank Herbert", "9780441172719", 1965, "Sci-Fi", 412,
                        "Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides, heir to a noble family tasked with ruling an inhospitable world where the only thing of value is the 'spice' melange, a drug capable of extending life and enhancing consciousness."),

                // 2. Tehnic (Bun pentru teste de "programare", "software", "best practices")
                createBook("Clean Code", "Robert C. Martin", "9780132350884", 2008, "Technical", 464,
                        "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees. This book is about good programming habits, naming conventions, and how to turn bad code into good code."),

                // 3. Clasic/Romantic (Bun pentru teste de "dragoste", "societate", "ironie")
                createBook("Pride and Prejudice", "Jane Austen", "9780141439518", 1813, "Romance", 279,
                        "Since its immediate success in 1813, Pride and Prejudice has remained one of the most popular novels in the English language. It charts the emotional development of the protagonist Elizabeth Bennet, who learns the error of making hasty judgments."),

                // 4. Istorie/Antropologie (Bun pentru teste de "umanitate", "evoluție")
                createBook("Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "9780062316097", 2011, "History", 443,
                        "One hundred thousand years ago, at least six different species of humans inhabited Earth. Yet today there is only one—homo sapiens. What happened to the others? And what may happen to us?"),

                // 5. Horror/Psihologic (Bun pentru teste de "frică", "izolare")
                createBook("The Shining", "Stephen King", "9780307743657", 1977, "Horror", 447,
                        "Jack Torrance's new job at the Overlook Hotel is the perfect chance for a fresh start. As the brutal winter weather sets in, the idyllic location feels ever more remote... and more sinister. And the only one to notice the strange and terrible forces gathering around the Overlook is Danny Torrance, a uniquely gifted five-year-old."),

                // 6. Fantasy (Bun pentru "magie", "aventură")
                createBook("Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "9780590353427", 1997, "Fantasy", 309,
                        "Harry Potter has never even heard of Hogwarts when the letters start dropping on the doormat at number four, Privet Drive. Addressed in green ink on yellowish parchment with a purple seal, they are swiftly confiscated by his grisly aunt and uncle."),

                // 7. Românesc (Bun pentru context local și "război")
                createBook("Ultima noapte de dragoste, întâia noapte de război", "Camil Petrescu", "9789734621340", 1930, "Classic", 350,
                        "Romanul analizează drama intelectualului Ștefan Gheorghidiu, sfâșiat între gelozia devorantă față de soția sa, Ela, și ororile Primului Război Mondial. Este o analiză lucidă a conștiinței umane în situații limită."),

                // 8. Distopie (Bun pentru "libertate", "supraveghere")
                createBook("1984", "George Orwell", "9780451524935", 1949, "Dystopian", 328,
                        "Among the seminal texts of the 20th century, Nineteen Eighty-Four is a rare work that grows more haunting as its futuristic purgatory becomes more real. Published in 1949, the book offers political satirist George Orwell's nightmarish vision of a totalitarian, bureaucratic world and one poor stiff's attempt to find individuality."),

                // 9. Carte fictivă pentru teste suplimentare
                createBook("Cronicile Brașovului Cyberpunk", "AI Mastermind", "9781234567897", 2025, "SF", 300, "În anul 2050, Brașovul a devenit capitala tehnologică a Europei. Urșii din Răcădău au fost înlocuiți cu drone de pază autonome, iar studenții de la Unitbv învață programare neuronală direct prin implanturi cerebrale. Protagonistul, un student la master, descoperă un server secret sub Tâmpa care controlează vremea.")
        );
    }

    private BookRequest createBook(String title, String author, String isbn, int year, String genre, int pages, String summary) {
        BookRequest req = new BookRequest();
        req.setTitle(title);
        req.setAuthor(author);
        req.setIsbn(isbn);
        req.setPublicationYear(year);
        req.setGenre(genre);
        req.setPageCount(pages);
        req.setSummary(summary);
        return req;
    }
}