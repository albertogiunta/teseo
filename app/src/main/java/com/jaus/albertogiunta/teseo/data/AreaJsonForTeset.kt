package com.jaus.albertogiunta.teseo.data

object AreaJsonForTeset {

//    val json = "{\n" +
//            "    \"id\": 1,\n" +
//            "    \"cells\": [\n" +
//            "        {\n" +
//            "            \"infoCell\": {\n" +
//            "                \"id\": 1,\n" +
//            "                \"uri\": \"uri1\",\n" +
//            "                \"name\": \"cell1\",\n" +
//            "                \"roomVertices\": {\n" +
//            "                    \"northWest\": {\n" +
//            "                        \"x\": 0,\n" +
//            "                        \"y\": 10\n" +
//            "                    },\n" +
//            "                    \"northEast\": {\n" +
//            "                        \"x\": 10,\n" +
//            "                        \"y\": 10\n" +
//            "                    },\n" +
//            "                    \"southWest\": {\n" +
//            "                        \"x\": 0,\n" +
//            "                        \"y\": 0\n" +
//            "                    },\n" +
//            "                    \"southEast\": {\n" +
//            "                        \"x\": 10,\n" +
//            "                        \"y\": 0\n" +
//            "                    }\n" +
//            "                },\n" +
//            "                \"antennaPosition\": {\n" +
//            "                    \"x\": 5,\n" +
//            "                    \"y\": 5\n" +
//            "                }\n" +
//            "            },\n" +
//            "            \"sensors\": [\n" +
//            "                {\n" +
//            "                    \"category\": 1,\n" +
//            "                    \"value\": 10.0\n" +
//            "                }\n" +
//            "            ],\n" +
//            "            \"neighbors\": [\n" +
//            "                {\n" +
//            "                    \"id\": 2,\n" +
//            "                    \"uri\": \"uri2\",\n" +
//            "                    \"name\": \"cell2\",\n" +
//            "                    \"roomVertices\": {\n" +
//            "                        \"northWest\": {\n" +
//            "                            \"x\": 10,\n" +
//            "                            \"y\": 8\n" +
//            "                        },\n" +
//            "                        \"northEast\": {\n" +
//            "                            \"x\": 20,\n" +
//            "                            \"y\": 8\n" +
//            "                        },\n" +
//            "                        \"southWest\": {\n" +
//            "                            \"x\": 10,\n" +
//            "                            \"y\": 2\n" +
//            "                        },\n" +
//            "                        \"southEast\": {\n" +
//            "                            \"x\": 20,\n" +
//            "                            \"y\": 2\n" +
//            "                        }\n" +
//            "                    },\n" +
//            "                    \"antennaPosition\": {\n" +
//            "                        \"x\": 15,\n" +
//            "                        \"y\": 5\n" +
//            "                    }\n" +
//            "                }\n" +
//            "            ],\n" +
//            "            \"passages\": [\n" +
//            "                {\n" +
//            "                    \"neighborId\": 2,\n" +
//            "                    \"startCoordinates\": {\n" +
//            "                        \"x\": 10,\n" +
//            "                        \"y\": 2\n" +
//            "                    },\n" +
//            "                    \"endCoordinates\": {\n" +
//            "                        \"x\": 10,\n" +
//            "                        \"y\": 8\n" +
//            "                    }\n" +
//            "                }\n" +
//            "            ],\n" +
//            "            \"isEntryPoint\": true,\n" +
//            "            \"isExitPoint\": false,\n" +
//            "            \"capacity\": 100,\n" +
//            "            \"squareMeters\": 50.0,\n" +
//            "            \"currentPeople\": 20,\n" +
//            "            \"practicability\": 5.0\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"infoCell\": {\n" +
//            "                \"id\": 2,\n" +
//            "                \"uri\": \"uri2\",\n" +
//            "                \"name\": \"cell2\",\n" +
//            "                \"roomVertices\": {\n" +
//            "                    \"northWest\": {\n" +
//            "                        \"x\": 10,\n" +
//            "                        \"y\": 8\n" +
//            "                    },\n" +
//            "                    \"northEast\": {\n" +
//            "                        \"x\": 20,\n" +
//            "                        \"y\": 8\n" +
//            "                    },\n" +
//            "                    \"southWest\": {\n" +
//            "                        \"x\": 10,\n" +
//            "                        \"y\": 2\n" +
//            "                    },\n" +
//            "                    \"southEast\": {\n" +
//            "                        \"x\": 20,\n" +
//            "                        \"y\": 2\n" +
//            "                    }\n" +
//            "                },\n" +
//            "                \"antennaPosition\": {\n" +
//            "                    \"x\": 15,\n" +
//            "                    \"y\": 5\n" +
//            "                }\n" +
//            "            },\n" +
//            "            \"sensors\": [\n" +
//            "                {\n" +
//            "                    \"category\": 1,\n" +
//            "                    \"value\": 30.0\n" +
//            "                }\n" +
//            "            ],\n" +
//            "            \"neighbors\": [\n" +
//            "                {\n" +
//            "                    \"id\": 1,\n" +
//            "                    \"uri\": \"uri1\",\n" +
//            "                    \"name\": \"cell1\",\n" +
//            "                    \"roomVertices\": {\n" +
//            "                        \"northWest\": {\n" +
//            "                            \"x\": 0,\n" +
//            "                            \"y\": 10\n" +
//            "                        },\n" +
//            "                        \"northEast\": {\n" +
//            "                            \"x\": 10,\n" +
//            "                            \"y\": 10\n" +
//            "                        },\n" +
//            "                        \"southWest\": {\n" +
//            "                            \"x\": 0,\n" +
//            "                            \"y\": 0\n" +
//            "                        },\n" +
//            "                        \"southEast\": {\n" +
//            "                            \"x\": 10,\n" +
//            "                            \"y\": 0\n" +
//            "                        }\n" +
//            "                    },\n" +
//            "                    \"antennaPosition\": {\n" +
//            "                        \"x\": 5,\n" +
//            "                        \"y\": 5\n" +
//            "                    }\n" +
//            "                },\n" +
//            "                {\n" +
//            "                    \"id\": 3,\n" +
//            "                    \"uri\": \"uri3\",  \n" +
//            "                    \"name\": \"cell3\",\n" +
//            "                    \"roomVertices\": {\n" +
//            "                        \"northWest\": {\n" +
//            "                            \"x\": 20,\n" +
//            "                            \"y\": 10\n" +
//            "                        },\n" +
//            "                        \"northEast\": {\n" +
//            "                            \"x\": 30,\n" +
//            "                            \"y\": 10\n" +
//            "                        },\n" +
//            "                        \"southWest\": {\n" +
//            "                            \"x\": 20,\n" +
//            "                            \"y\": 0\n" +
//            "                        },\n" +
//            "                        \"southEast\": {\n" +
//            "                            \"x\": 30,\n" +
//            "                            \"y\": 0\n" +
//            "                        }\n" +
//            "                    },\n" +
//            "                    \"antennaPosition\": {\n" +
//            "                        \"x\": 15,\n" +
//            "                        \"y\": 5\n" +
//            "                    }\n" +
//            "                }\n" +
//            "            ],\n" +
//            "            \"passages\": [\n" +
//            "                {\n" +
//            "                    \"neighborId\": 0,\n" +
//            "                    \"startCoordinates\": {\n" +
//            "                        \"x\": 0,\n" +
//            "                        \"y\": 4\n" +
//            "                    },\n" +
//            "                    \"endCoordinates\": {\n" +
//            "                        \"x\": 0,\n" +
//            "                        \"y\": 6\n" +
//            "                    }\n" +
//            "                },\n" +
//            "                {\n" +
//            "                    \"neighborId\": 1,\n" +
//            "                    \"startCoordinates\": {\n" +
//            "                        \"x\": 10,\n" +
//            "                        \"y\": 2\n" +
//            "                    },\n" +
//            "                    \"endCoordinates\": {\n" +
//            "                        \"x\": 10,\n" +
//            "                        \"y\": 8\n" +
//            "                    }\n" +
//            "                }\n" +
//            "            ],\n" +
//            "            \"isEntryPoint\": false,\n" +
//            "            \"isExitPoint\": true,\n" +
//            "            \"capacity\": 30,\n" +
//            "            \"squareMeters\": 20.0,\n" +
//            "            \"currentPeople\": 10,\n" +
//            "            \"practicability\": 2.0\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"infoCell\": {\n" +
//            "                \"id\": 3,\n" +
//            "                \"uri\": \"uri3\",\n" +
//            "                \"name\": \"cell3\",\n" +
//            "                \"roomVertices\": {\n" +
//            "                    \"northWest\": {\n" +
//            "                        \"x\": 20,\n" +
//            "                        \"y\": 10\n" +
//            "                    },\n" +
//            "                    \"northEast\": {\n" +
//            "                        \"x\": 30,\n" +
//            "                        \"y\": 10\n" +
//            "                    },\n" +
//            "                    \"southWest\": {\n" +
//            "                        \"x\": 20,\n" +
//            "                        \"y\": 0\n" +
//            "                    },\n" +
//            "                    \"southEast\": {\n" +
//            "                        \"x\": 30,\n" +
//            "                        \"y\": 0\n" +
//            "                    }\n" +
//            "                },\n" +
//            "                \"antennaPosition\": {\n" +
//            "                    \"x\": 25,\n" +
//            "                    \"y\": 5\n" +
//            "                }\n" +
//            "            },\n" +
//            "            \"sensors\": [\n" +
//            "                {\n" +
//            "                    \"category\": 1,\n" +
//            "                    \"value\": 30.0\n" +
//            "                }\n" +
//            "            ],\n" +
//            "            \"neighbors\": [\n" +
//            "                {\n" +
//            "                    \"id\": 2,\n" +
//            "                    \"uri\": \"uri2\",\n" +
//            "                    \"name\": \"cell2\",\n" +
//            "                    \"roomVertices\": {\n" +
//            "                        \"northWest\": {\n" +
//            "                            \"x\": 10,\n" +
//            "                            \"y\": 8\n" +
//            "                        },\n" +
//            "                        \"northEast\": {\n" +
//            "                            \"x\": 20,\n" +
//            "                            \"y\": 8\n" +
//            "                        },\n" +
//            "                        \"southWest\": {\n" +
//            "                            \"x\": 10,\n" +
//            "                            \"y\": 2\n" +
//            "                        },\n" +
//            "                        \"southEast\": {\n" +
//            "                            \"x\": 20,\n" +
//            "                            \"y\": 2\n" +
//            "                        }\n" +
//            "                    },\n" +
//            "                    \"antennaPosition\": {\n" +
//            "                        \"x\": 15,\n" +
//            "                        \"y\": 5\n" +
//            "                    }\n" +
//            "                }\n" +
//            "            ],\n" +
//            "            \"passages\": [\n" +
//            "                {\n" +
//            "                    \"neighborId\": 0,\n" +
//            "                    \"startCoordinates\": {\n" +
//            "                        \"x\": 24,\n" +
//            "                        \"y\": 0\n" +
//            "                    },\n" +
//            "                    \"endCoordinates\": {\n" +
//            "                        \"x\": 26,\n" +
//            "                        \"y\": 0\n" +
//            "                    }\n" +
//            "                },\n" +
//            "                {\n" +
//            "                    \"neighborId\": 1,\n" +
//            "                    \"startCoordinates\": {\n" +
//            "                        \"x\": 20,\n" +
//            "                        \"y\": 2\n" +
//            "                    },\n" +
//            "                    \"endCoordinates\": {\n" +
//            "                        \"x\": 20,\n" +
//            "                        \"y\": 8\n" +
//            "                    }\n" +
//            "                }\n" +
//            "            ],\n" +
//            "            \"isEntryPoint\": false,\n" +
//            "            \"isExitPoint\": true,\n" +
//            "            \"capacity\": 30,\n" +
//            "            \"squareMeters\": 20.0,\n" +
//            "            \"currentPeople\": 10,\n" +
//            "            \"practicability\": 2.0\n" +
//            "        }\n" +
//            "    ]\n" +
//            "}"

}