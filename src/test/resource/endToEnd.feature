Feature: Collects prices from market, modifies them and provides customers with the latest price.

  Scenario Outline: convert CSV to Json and apply commission
    Given CSV file with price instance
    When I process the file
    Then all prices are processed in sequence
    And commission bid <commission_bid> and commission ask <commission_ask> are applied successfully
    Examples:
      | commission_bid              | commission_ask |
      | 0.001                       |   0.001        |
      | 0.005                       |   0.002        |



  Scenario Outline: Filter Json by name and get the latest price
    Given Json data
    Then I can filter Json for latest price for instrument <name>
    And get the latest Price
    Examples:
      | name    |
      | EUR/USD |
      | EUR/JPY |
      | GBP/USD |