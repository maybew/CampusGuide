from django.http import HttpResponse
from django.core import serializers
import json
from models import Building


def all_buildings(request):
    # data = [building for building in Building.objects.values()]
    # json_data = json.dumps(data)
    buildings = Building.objects.all()
    json_data = serializers.serialize("json", buildings, use_natural_keys=True)
    return HttpResponse(json_data, content_type="application/json")

