<template>
  <v-row>
  <v-col>
    <span
        v-html="convertMarkDown('First Group Items:')"
    />
  <ul>
    <li v-for="item in questionDetails.firstGroup" :key="item.sequence">
      <span
          v-html="
          convertMarkDown(item.content)"
      />
      <ul>
        <li v-for="itemLinks in item.itemSequenceSet" :key="itemLinks.sequence">
           <span
               v-html="convertMarkDown(itemLinks + ': ' + questionDetails.secondGroup[itemLinks].content)"
           />
        </li>
      </ul>
    </li>
  </ul>
      <span
      v-html="convertMarkDown('Second Group Items:')"
      />
      <ul>
        <li v-for="item in questionDetails.secondGroup" :key="item.sequence">
      <span
          v-html="
          convertMarkDown(item.content)"
      />
        </li>
      </ul>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';

@Component
export default class ItemCombinationView extends Vue{
  @Prop() readonly questionDetails!: ItemCombinationQuestionDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>


