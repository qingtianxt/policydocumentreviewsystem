package stdu.wzw.policydocumentreviewsystem;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import stdu.wzw.model.EsPolicy;
import stdu.wzw.repository.EsPolicyRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
public class EsPolicyTest {
    @Autowired
    private EsPolicyRepository esPolicyRepository;

    public void testfindByTitleLikeOrContentLikeOrSummaryLike() {
        esPolicyRepository.deleteAll();

        Pageable pageable = new PageRequest(0, 20);
        String name = "安装";
        /*Page<EsPolicy> page = esPolicyRepository.findByTitleLikeOrContentLikeOrSummaryLike(name, name, name, pageable);
        for (EsPolicy es : page.getContent()
                ) {
            System.out.println("esPolicy:" + es);
        }*/


    }

}
