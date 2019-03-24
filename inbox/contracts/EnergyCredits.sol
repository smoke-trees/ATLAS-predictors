pragma solidity ^0.4.17;

contract Credits {
    uint256 public CURRENT_CREDIT_PRICE;
    address public admin;

    mapping(address => uint256) customerList;

    function setCreditPrice(uint256 newPrice) public restricted {
        CURRENT_CREDIT_PRICE = newPrice;
    }

    modifier restricted() {
        require(msg.sender == admin);
        _;
    }

    function Credits(uint256 initPrice) public {
        CURRENT_CREDIT_PRICE = initPrice;
    }

    function getBalance() public view returns(uint256) {
        return customerList[msg.sender];
    }

    function consumeCredits(uint256 _creditsAmount)

    function purchaseCredits(uint256 _creditsAmount) public payable {
        require(msg.value >= _creditsAmount*CURRENT_CREDIT_PRICE);
        customerList[msg.sender] += _creditsAmount;
    }
}