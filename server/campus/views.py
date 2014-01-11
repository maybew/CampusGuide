from django.http import HttpResponse
from django.core import serializers
from django.utils import simplejson
from models import Building

def all_buildings(request):
    data = [building for building in Building.objects.values()]
    json = simplejson.dumps(data)
    # buildings = Building.objects.all()
    # json = serializers.serialize("json", buildings)
    return HttpResponse(json, content_type="application/json")