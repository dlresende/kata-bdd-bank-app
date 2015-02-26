package net.diegolemos.bankapp.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.diegolemos.bankapp.account.Account;
import net.diegolemos.bankapp.client.Client;

import javax.ws.rs.client.WebTarget;

import static javax.ws.rs.client.Entity.json;
import static net.diegolemos.bankapp.client.ClientBuilder.aClient;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AccountStepdefs extends AbstractStepdefs {

    private final WebTarget clientResource = resource("client");
    private final WebTarget accountResource = resource("account");

    private Client client;

    @Given("^an existing client named \"([^\"]*)\"$")
    public void an_existing_client_named(String username) throws Throwable {
        client = aClient().withUsername(username).build();
        clientResource.path(username).request().put(json(client));
    }

    @When("^he opens a bank account$")
    public void he_opens_a_bank_account() throws Throwable {
        Account userAccount = new Account(client);
        accountResource.request().put(json(userAccount));
    }

    @Then("^the (?:new|initial) balance is ([^\"]*) EUR$")
    public void the_balance_is_EUR(double expectedBalance) throws Throwable {
        Account account = accountResource.path(client.username()).request().get(Account.class);
        assertThat(account.balance(), is(expectedBalance));
    }

    @Given("^an existing client named \"([^\"]*)\" with ([^\"]*) EUR in her account$")
    public void an_existing_client_named_with_EUR_in_her_account(String username, double balance) throws Throwable {
        an_existing_client_named(username);
        he_opens_a_bank_account();
        the_balance_is_EUR(balance);
    }

    @When("^she deposits ([^\"]*) EUR into her account$")
    public void she_deposits_EUR_into_her_account(double amount) throws Throwable {
        Account account = accountResource.path(client.username()).request().get(Account.class);
        try {
            account.deposit(amount);
            accountResource.request().put(json(account));
        } catch (IllegalStateException ignored) {
        }
    }
}
