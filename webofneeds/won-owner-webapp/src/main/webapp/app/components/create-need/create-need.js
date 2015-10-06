/**
 * Created by ksinger on 24.08.2015.
 */
;

import angular from 'angular'
import 'ng-redux';
import createNeedTitleBarModule from '../create-need-title-bar';
import posttypeSelectModule from '../posttype-select';
import labelledHrModule from '../labelled-hr';
import dynamicTextfieldModule from '../dynamic-textfield';
import imageDropzoneModule from '../image-dropzone';
//import draftStoreModule from '../../stores/draft-store';
import { attach } from '../../utils';
import { actionCreators }  from '../../actions';

const postTypeTexts = [
    {
        type: won.WON.BasicNeedTypeDemandCompacted,
        text: 'I want to have something',
        helpText: 'Use this type in case (want) foo sam quam aspic temod et que in prendiae perovidel.',
    },
    {
        type: won.WON.BasicNeedTypeSupplyCompacted,
        text: 'I offer something',
        helpText: 'Use this type in case (offer) case sam quam aspic temod et que in prendiae perovidel.'
    },
    {
        type: won.WON.BasicNeedTypeDotogetherCompacted,
        text: 'I want to do something together',
        helpText: 'Use this type in case case (together) sam quam aspic temod et que in prendiae perovidel.'
    },
    {
        type: won.WON.BasicNeedTypeCritiqueCompacted,
        text: 'I want to change something',
        helpText: 'Use this type in case (change) case sam quam aspic temod et que in prendiae perovidel.'
    }
]

//TODO can't inject $scope with the angular2-router, preventing redux-cleanup
const serviceDependencies = ['$q', '$ngRedux', /*'$routeParams' /*injections as strings here*/];

class CreateNeedController {
    constructor(/* arguments <- serviceDependencies */) {
        attach(this, serviceDependencies, arguments);

        this.postTypeTexts = postTypeTexts;

        //TODO debug; deleteme
        window.cnc = this;

        //this.titlePicZoneNg().bind('click', e => 0);
        //this.titlePicZone().addEventListener('click', e => 0);
        //this.titlePicZone().addEventListener('drop', e => 0);

        const selectFromState = (state) => ({
            wubs: state.get('wubs'),
            //drafts: state.get('drafts')
        });

        // Using actionCreators like this means that every action defined there is available in the template.
        const unsubscribe = this.$ngRedux.connect(selectFromState, actionCreators)(this);
        //this.$scope.$on('$destroy', unsubscribe); //can't inject $scope with angular2-router as it doesn't exist anymore in angular2

    }

    selectType(typeIdx) {
        console.log('selected type ', postTypeTexts[typeIdx].type);
        const draftIdx = this.$ngRedux.getState().get('drafts').get('activeDraftIdx');
        //const draftIdx = this.drafts.get('activeDraftIdx');
        this.drafts__change__type({idx: draftIdx, type: postTypeTexts[typeIdx].type}); //TODO proper draft idx
    }
    unselectType() {
        console.log('unselected type ');
        this.drafts__change__type({idx: draftIdx, type: undefined}); //TODO proper draft idx
    }
    titlePicZoneNg() {
        if(!this._titlePicZone) {
            this._titlePicZone = this.$element.find('#titlePic');
        }
        return this._titlePicZone;
    }
    titlePicZone() {
        return titlePicZoneNg[0];
    }

}

//CreateNeedController.$inject = serviceDependencies;

export default angular.module('won.owner.components.createNeed', [
        createNeedTitleBarModule,
        posttypeSelectModule,
        labelledHrModule,
        dynamicTextfieldModule,
        imageDropzoneModule,
    ])
    //.controller('CreateNeedController', [...serviceDependencies, CreateNeedController])
    .controller('CreateNeedController', [...serviceDependencies, CreateNeedController])
    .name;
