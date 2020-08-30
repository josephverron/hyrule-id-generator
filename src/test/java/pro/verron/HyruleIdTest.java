package pro.verron;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.fail;

class Hyrule_Id_Tests {

    @Test
    void should_be_able_to_create_an_hyrule_id_producer(){
        assertThat("Failed to create an HyruleId producer", Hyrule.idStream(), notNullValue(Stream.class));
    }

    @Test
    void should_be_9_characters_long_only_be_composed_of_digits(){
        Optional<HyruleId> id = Hyrule.idStream().findFirst();
        if(id.isEmpty())
            fail("Failed to produce an id");
        assertThat(id.get().representation(), matchesPattern("[0-9]{9}"));
    }

    @Test
    void should_be_comprised_of_its_seed_value_left_padded_by_zero(){
        HyruleId id = HyruleId.of(13579);
        assertThat(id.representation(), is(equalTo("000013579")));
    }

    @Test
    void should_be_sequentially_different(){
        List<HyruleId> list = Hyrule.idStream()
                .limit(10_000)
                .collect(toList());
        Set<HyruleId> set = new HashSet<>(list);
        assertThat(set.size(), is(equalTo(list.size())));
    }

    @Test
    void should_be_equals_if_their_seed_is_the_same(){
        HyruleId first = HyruleId.of(123456789);
        HyruleId second = HyruleId.of(123456789);
        assertThat(first, is(equalTo(second)));
    }

    @Test
    void should_be_different_if_their_seed_is_different(){
        HyruleId first = HyruleId.of(123456789);
        HyruleId second = HyruleId.of(987654321);
        assertThat(first, is(not(equalTo(second))));
    }

    @Test
    void should_be_able_to_generate_a_large_number_of_ids(){
        Stream<HyruleId> stream = Hyrule.idStream();
        Optional<HyruleId> id = stream.skip(10_000).findFirst();
        assertThat("Not found that much id", id.isPresent());
    }

    @Test
    void should_not_be_ordered_ascending(){
        Stream<HyruleId> stream = Hyrule.idStream();
        List<HyruleId> ids = stream.limit(10_000).collect(toList());
        List<HyruleId> sortedIds = ids.stream().sorted().collect(toList());
        assertThat(sortedIds, is(not(equalTo(ids))));
    }

    @Test
    void should_not_be_ordered_descending(){
        Stream<HyruleId> stream = Hyrule.idStream();
        List<HyruleId> ids = stream.limit(10_000).collect(toList());
        List<HyruleId> sortedIds = ids.stream().sorted(Comparator.reverseOrder()).collect(toList());
        assertThat(sortedIds, is(not(equalTo(ids))));
    }


}
