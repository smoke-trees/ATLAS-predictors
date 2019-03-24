pragma solidity ^0.4.17;

contract Election {
    struct Candidate {
        uint id;
        string name;
        uint voteCount;
    }

    address contractCreator;
    mapping(address => bool) public voters;
    mapping(uint => Candidate) public candidates;

    uint public candidatesCount;


    function Election() public {
        contractCreator = msg.sender;
        addCandidate("BJP");
        addCandidate("INC");
        addCandidate("AAP");
        addCandidate("SIP");
        addCandidate("NOTA");
    }

    function addCandidate(string _name) private {
        candidatesCount++;
        candidates[candidatesCount] = Candidate(candidatesCount, _name, 0);
    }

    function vote(uint _candidateId) public {
        require(!voters[msg.sender]);
        require(_candidateId > 0 && _candidateId <= candidatesCount);
        voters[msg.sender] = true;
        candidates[_candidateId].voteCount++;
    }


}