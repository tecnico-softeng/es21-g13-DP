<template>
  <div id="app">
    <v-app id="inspire">
      <v-container class="grey lighten-5">
        <v-row
            v-for="n in 1"
            :key="n"
            :class="n === 1 ? 'mb-6' : ''"
            no-gutters
        >
          <v-col>
            <v-card
                class="pa-2"
                outlined
                tile
            >
              Group 1
              <div class="item-combination-items-first-group">


                <v-row
                    v-for="(item, index) in sQuestionDetails.firstGroup"
                    :key="index"
                    data-cy="questionFirstGroupItemsInput"
                >
                  <v-col cols="10">
                    <v-textarea
                        v-model="item.content"
                        :label="`Item ${index + 1}`"
                        :data-cy="`Item1${index + 1}`"
                        rows="1"
                        auto-grow
                    ></v-textarea>
                  </v-col>

                  <v-col v-if="sQuestionDetails.firstGroup.length > 1">
                    <v-tooltip bottom>
                      <template v-slot:activator="{ on }">
                        <v-icon
                            :data-cy="`Delete${index + 1}`"
                            small
                            class="ma-1 action-button"
                            v-on="on"
                            @click="removeItemFirstGroup(index)"
                            color="red"
                        >close</v-icon
                        >
                      </template>
                      <span>Remove Item</span>
                    </v-tooltip>
                  </v-col>
                </v-row>

                <v-row>
                  <v-btn
                      class="ma-auto"
                      color="blue darken-1"
                      @click="addItemFirstGroup()"
                      data-cy="addItemsFirstGroupItemCombination"
                  >Add Item</v-btn>
                </v-row>
              </div>
            </v-card>
          </v-col>
          <!-- GRUPO 2 A PARTIR DAQUI -->
          <v-col>
            <v-card
                class="pa-2"
                outlined
                tile
            >
              Group 2
              <div class="item-combination-items-second-group">


                <v-row
                    v-for="(item, index) in sQuestionDetails.secondGroup"
                    :key="index"
                    data-cy="questionSecondGroupItemsInput"
                >
                  <v-col cols="10">
                    <v-textarea
                        v-model="item.content"
                        :label="`Item ${index + 1}`"
                        :data-cy="`Item2${index + 1}`"
                        rows="1"
                        auto-grow
                    ></v-textarea>
                  </v-col>

                  <v-col v-if="sQuestionDetails.secondGroup.length > 1">
                    <v-tooltip bottom>
                      <template v-slot:activator="{ on }">
                        <v-icon
                            :data-cy="`Delete${index + 1}`"
                            small
                            class="ma-1 action-button"
                            v-on="on"
                            @click="removeItemSecondGroup(index)"
                            color="red"
                        >close</v-icon
                        >
                      </template>
                      <span>Remove Item</span>
                    </v-tooltip>
                  </v-col>
                </v-row>

                <v-row>
                  <v-btn
                      class="ma-auto"
                      color="blue darken-1"
                      @click="addItemSecondGroup()"
                      data-cy="addItemsSeconGroupItemCombination"
                  >Add Item</v-btn
                  >
                </v-row>
              </div>
            </v-card>
          </v-col>
        </v-row>
          <!-- LINKS AQUI -->
          <div class="item-combination-links">
            <v-row v-for="(item, index) in this.sQuestionDetails.firstGroup"
            :key="index">
              <v-col
                  cols="12"
                  sm="5"
              >
                <v-subheader v-text="'Item ' + (index+1) + ' links:'"></v-subheader>
              </v-col>
              <v-col
                  cols="12"
                  sm="6"
              >
                <v-select
                    v-model="item.linked"
                    :items="sQuestionDetails.secondGroup"
                    item-text="content"
                    item-value="content"
                    label="Select"
                    multiple
                    :data-cy="`addLinksItemCombination${index+1}`"
                    chips
                    hint="What are the links"
                    persistent-hint
                ></v-select>

              </v-col>
            </v-row>

          </div>
        <div>
          <v-row>
            <v-col>
              <v-btn
                  class="ma-auto"
                  color="blue darken-1"
                  @click="saveLinks()"
                  data-cy="saveLinksItemCombination"
              >Store Links</v-btn
              >
            </v-col>
          </v-row>
        </div>


      </v-container>
    </v-app>
  </div>
</template>

<script lang="ts">
import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import Item from '@/models/management/Item';

@Component
export default class ItemCombinationCreate extends Vue {
  @PropSync('questionDetails', { type: ItemCombinationQuestionDetails })
  sQuestionDetails!: ItemCombinationQuestionDetails;

  addItemFirstGroup() {
    let sItem:Item =  new Item();
    sItem.sequence = this.sQuestionDetails.firstGroup.length;
    this.sQuestionDetails.firstGroup.push(sItem);
  }

  addItemSecondGroup() {
    let sItem:Item =  new Item();
    sItem.sequence = this.sQuestionDetails.secondGroup.length;
    this.sQuestionDetails.secondGroup.push(sItem);
  }

  removeItemFirstGroup(index: number) {
    const seq:number = this.sQuestionDetails.firstGroup[index].sequence;
    for (let itemSecondGroup of this.sQuestionDetails.secondGroup) {
      const ind: number = itemSecondGroup.itemSequenceSet.indexOf(seq);
      if (ind != -1) {
        itemSecondGroup.itemSequenceSet.splice(ind, 1);
      }
    }
    for (let item of this.sQuestionDetails.firstGroup){
      if (item.sequence > seq){
        item.sequence--;
      }
    }
    this.sQuestionDetails.firstGroup.splice(index, 1);
  }

  removeItemSecondGroup(index: number) {
    const seq:number = this.sQuestionDetails.secondGroup[index].sequence;
    const conteudo:string = this.sQuestionDetails.secondGroup[index].content;
    for (let itemFirstGroup of this.sQuestionDetails.firstGroup){
      const seqInd:number = itemFirstGroup.itemSequenceSet.indexOf(seq);
      const linkInd:number = itemFirstGroup.linked.indexOf(conteudo);
      if (seqInd != -1){
        itemFirstGroup.itemSequenceSet.splice(seqInd, 1);
      }
      if (linkInd != -1){
        itemFirstGroup.linked.splice(seqInd, 1);
      }
    }
    for (let item of this.sQuestionDetails.secondGroup){
      if (item.sequence > seq){
        item.sequence--;
      }
    }
    this.sQuestionDetails.secondGroup.splice(index, 1);
  }

  saveLinks(){
    for (let secondItem of this.sQuestionDetails.secondGroup){
      secondItem.itemSequenceSet = [];
    }
    for (let item of this.sQuestionDetails.firstGroup){
      const links:number[] = [];
      for (let linkedContent of item.linked){
        let id:number = this.sQuestionDetails.secondGroup.indexOf(this.sQuestionDetails.secondGroup.filter(it => it.content === linkedContent)[0]);
        id === -1? id = 0:links.push(id);
      }
      item.itemSequenceSet=links;
      for (let linkedItem of item.itemSequenceSet){
        this.sQuestionDetails.secondGroup[linkedItem].itemSequenceSet.push(this.sQuestionDetails.firstGroup.indexOf(item));
      }
    }
  }


}
</script>

<style scoped>

</style>