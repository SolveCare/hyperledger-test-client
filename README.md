cd hyperledger-test-client/src/main/resources/health
export FABRIC_CFG_PATH=$PWD

./bin/configtxgen -profile ThreeOrgsOrdererGenesis -outputBlock ./channel-artifacts/genesis.block
./bin/configtxgen -profile ThreeOrgsChannel -outputCreateChannelTx ./channel-artifacts/health-channel.tx -channelID health-channel

./bin/configtxgen -profile ThreeOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/ClinicMSPanchors.tx -channelID health-channel -asOrg ClinicMSP
./bin/configtxgen -profile ThreeOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/HumanMSPanchors.tx -channelID health-channel -asOrg HumanMSP
./bin/configtxgen -profile ThreeOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/InsuranceMSPanchors.tx -channelID health-channel -asOrg InsuranceMSP