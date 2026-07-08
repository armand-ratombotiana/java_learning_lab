package com.learning.backend22.resolver;

import com.learning.backend22.model.Show;
import com.learning.backend22.service.ShowService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.List;

@DgsComponent
public class ShowResolver {

    private final ShowService showService;

    public ShowResolver(ShowService showService) {
        this.showService = showService;
    }

    @DgsQuery
    public List<Show> shows() {
        return showService.getAllShows();
    }

    @DgsQuery
    public Show show(@InputArgument String id) {
        return showService.getShow(id);
    }
}
