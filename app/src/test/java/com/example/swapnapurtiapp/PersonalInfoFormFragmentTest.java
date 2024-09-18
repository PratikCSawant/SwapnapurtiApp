package com.example.swapnapurtiapp;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;

import org.junit.Test;

public class PersonalInfoFormFragmentTest  {

    @Test
    public void checkAdd()
    {
        PersonalInfoFormFragment obj = new PersonalInfoFormFragment();
        assertEquals(4, obj.add(2,2));
    }
}