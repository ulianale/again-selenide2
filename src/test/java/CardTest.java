import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class CardTest {

    public LocalDate ifWeekend(int days) { // метод,  если дата выпадает на выходные дни
        LocalDate day = LocalDate.now().plusDays(days);
        if (day.getDayOfWeek().getValue() == 6) {
            day = day.plusDays(2);
        } else if (day.getDayOfWeek().getValue() == 7) {
            day = day.plusDays(1);
        }
        return day;
    }

    public String data(int days) { // метод определения даты встречи
        return ifWeekend(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public void city(String selectCity) {
        $("[data-test-id=city] input").setValue(selectCity.substring(0, 2));
        $$(".menu-item__control").findBy(Condition.text(selectCity)).click();

    }

    public void selectDayInWeek() { //  выбор даты из календаря

        int date = LocalDate.now().getDayOfMonth();
        int dayInWeek = ifWeekend(7).getDayOfMonth();
        String day = "" + dayInWeek;

        $("[placeholder='Дата встречи'").click();
        if (dayInWeek > date) {
            $$(".calendar__day").findBy(text(day)).click();
        } else {
            $$("..calendar__arrow_direction_right").last().click();
            $$(".calendar__day").findBy(text(day)).click();
        }

    }

    @BeforeEach
    void setUp() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
    }

    @Test
    void shouldValidForm() {
        $("[data-test-id=city] input").setValue("Казань");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue(data(3));
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79091001213");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(".notification").shouldHave(Condition.exactText("Успешно! Встреча успешно забронирована на " + data(3)), Duration.ofSeconds(15));

    }

    @Test
    void shouldSelectCity() {
        //$("[data-test-id=city] input").setValue("Го");
        //$x("//span[contains(text(),'Го')]").click();
        city("Москва");
        selectDayInWeek();
        $("[data-test-id=name] input").setValue("Анна Анна");
        $("[data-test-id=phone] input").setValue("+79091001213");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(".notification").shouldHave(Condition.exactText("Успешно! Встреча успешно забронирована на " + data(7)), Duration.ofSeconds(15));

    }

}
