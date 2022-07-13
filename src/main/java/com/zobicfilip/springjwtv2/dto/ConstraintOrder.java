package com.zobicfilip.springjwtv2.dto;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

public abstract class ConstraintOrder {
    @GroupSequence({
            First.class,
            Default.class,
            Second.class,
            Third.class,
            Fourth.class,
            Fifth.class,
            Sixth.class,
            Seventh.class,
            Eight.class})
    public interface ValidationSequence { }
    interface First { }
    interface Second { }

    interface Third { }

    interface Fourth { }

    interface Fifth { }

    interface Sixth { }

    interface Seventh { }
    interface Eight { }
}
