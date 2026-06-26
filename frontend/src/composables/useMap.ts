import { ref } from 'vue';


export function useMap() {
    const mapInstance = ref<any>(null);
    const apiInstance = ref<any>(null);

    const onMapReady = ({ map, api }: { map: any; api: any }) => {
        mapInstance.value = map;
        apiInstance.value = api;
    };



    return {
        // fetchMap,
        onMapReady,
        // mapUrl,
        mapInstance,
        apiInstance,
    };

}