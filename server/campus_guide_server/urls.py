from django.conf.urls import patterns, include, url
from campus_guide_server import settings
from campus.views import all_buildings

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'campus_guide_server.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    
    url(r'^media/(?P<path>.*)$', 'django.views.static.serve', {
            'document_root': settings.MEDIA_ROOT,
        }),
                       
    url(r'^all/$', all_buildings),
)
